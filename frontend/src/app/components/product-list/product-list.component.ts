import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  signal,
} from "@angular/core"
import { CommonModule } from "@angular/common"
import { Router } from "@angular/router"
import { ProductService } from "../../services/product.service"
import { ErrorService } from "../../services/error.service"
import { Product, ProductResponse } from "../../models/product.model"
import { Subject } from "rxjs"
import { takeUntil } from "rxjs/operators"

@Component({
  selector: "app-product-list",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./product-list.component.html",
  styleUrls: ["./product-list.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProductListComponent implements OnInit, OnDestroy {
  products = signal<Product[]>([])
  loading = signal(false)
  currentPage = signal(0) 
  pageSize = 10
  totalPages = signal(0)
  error = signal<string | null>(null)

  private destroy$ = new Subject<void>()

  constructor(
    private productService: ProductService,
    private errorService: ErrorService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadProducts()
    this.subscribeToErrors()
  }

  loadProducts(): void {
    this.errorService.setLoading(true)
    this.loading.set(true)

    const pageToSend = this.currentPage() + 1

    this.productService
      .getProducts(pageToSend, this.pageSize)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: ProductResponse) => {
          console.log("Productos recibidos:", response)
          this.products.set(response.data)
          if (response.meta) {
            this.totalPages.set(response.meta.pages)
            
            this.currentPage.set(response.meta.current_page - 1)
          }
          this.errorService.setLoading(false)
          this.loading.set(false)
          this.error.set(null)
        },
        error: (error: any) => {
          console.error("Error loading products:", error)
          this.errorService.setLoading(false)
          this.loading.set(false)
          this.error.set("Error al cargar productos. Por favor intenta de nuevo.")
        },
      })
  }

  private subscribeToErrors(): void {

  }

  getError(): string | null {
    return this.errorService.getError()
  }

  getLoading(): boolean {
    return this.errorService.isLoading()
  }

  viewDetails(productId: number): void {
    this.router.navigate(["/products", productId])
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update((p) => p - 1)
      this.loadProducts()
    }
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update((p) => p + 1)
      this.loadProducts()
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next()
    this.destroy$.complete()
  }
}
