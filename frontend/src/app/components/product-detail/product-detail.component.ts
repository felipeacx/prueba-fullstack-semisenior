import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, signal } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"
import { ActivatedRoute, Router } from "@angular/router"
import { ProductService } from "../../services/product.service"
import { ErrorService } from "../../services/error.service"
import { Product } from "../../models/product.model"
import { Subject } from "rxjs"
import { takeUntil } from "rxjs/operators"

@Component({
  selector: "app-product-detail",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./product-detail.component.html",
  styleUrls: ["./product-detail.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProductDetailComponent implements OnInit, OnDestroy {
  product = signal<Product | null>(null)
  inventory = signal<number | null>(null)
  loading = signal(false)
  error = signal<string | null>(null)
  quantity = signal(1)
  purchasing = signal(false)
  purchaseSuccess = signal(false)

  private destroy$ = new Subject<void>()

  constructor(
    private productService: ProductService,
    private errorService: ErrorService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.subscribeToErrors()
    this.route.params.pipe(takeUntil(this.destroy$)).subscribe((params) => {
      const productId = +params["id"] 
      if (productId) {
        this.loadProduct(productId)
      }
    })
  }

  private loadProduct(productId: number): void {
    console.log("Iniciando carga de producto:", productId)
    this.errorService.setLoading(true)
    this.loading.set(true)
    this.productService
      .getProductById(productId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (product: Product) => {
          console.log("Producto cargado exitosamente:", product)
          this.product.set(product)
          this.loadInventory(productId)
        },
        error: (error: any) => {
          console.error("Error loading product:", error)
          this.error.set("Failed to load product details")
          this.errorService.setLoading(false)
          this.loading.set(false)
        },
      })
  }

  private loadInventory(productId: number): void {
    console.log("Iniciando carga de inventario para producto:", productId)
    this.productService
      .getProductInventory(productId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data: { quantity: number }) => {
          console.log("Inventario cargado exitosamente:", data)
          this.inventory.set(data.quantity)
          this.errorService.setLoading(false)
          this.loading.set(false)
        },
        error: (error: any) => {
          console.error("Error loading inventory:", error)
          this.inventory.set(0)
          this.errorService.setLoading(false)
          this.loading.set(false)
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

  purchaseProduct(): void {
    const product = this.product()
    const quantity = this.quantity()
    const inventory = this.inventory()

    if (!product || quantity <= 0 || quantity > (inventory || 0)) {
      this.error.set("Invalid quantity")
      return
    }

    this.purchasing.set(true)
    this.purchaseSuccess.set(false)
    this.errorService.setLoading(true)

    this.productService
      .purchaseProduct({
        productId: product.id,
        quantity: quantity,
      })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          console.log("✅ Purchase response received:", response)
          if (response.success) {
            this.purchaseSuccess.set(true)
            this.error.set(null)
            if (response.remainingQuantity !== undefined && response.remainingQuantity !== null) {
              console.log(
                `📦 Updating inventory from ${this.inventory()} to ${response.remainingQuantity}`
              )
              this.inventory.set(response.remainingQuantity)
            }
            
            setTimeout(() => {
              this.router.navigate(["/products"])
            }, 2000)
          } else {
            this.error.set(response.message || "Purchase failed")
          }
          this.purchasing.set(false)
          this.errorService.setLoading(false)
        },
        error: (error: Error) => {
          console.error("❌ Error purchasing product:", error)
          this.error.set("Failed to complete purchase")
          this.purchasing.set(false)
          this.errorService.setLoading(false)
        },
      })
  }

  increaseQuantity(): void {
    const current = this.quantity()
    const inv = this.inventory()
    if (inv && current < inv) {
      this.quantity.update((q) => q + 1)
    }
  }

  decreaseQuantity(): void {
    if (this.quantity() > 1) {
      this.quantity.update((q) => q - 1)
    }
  }

  goBack(): void {
    this.router.navigate(["/products"])
  }

  ngOnDestroy(): void {
    this.destroy$.next()
    this.destroy$.complete()
  }
}
