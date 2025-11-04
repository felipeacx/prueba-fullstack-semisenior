import { Injectable, signal, computed, effect } from "@angular/core"
import { HttpClient, HttpParams } from "@angular/common/http"
import { Observable } from "rxjs"
import { map } from "rxjs/operators"
import {
  Product,
  ProductResponse,
  ProductDetailResponse,
  PurchaseRequest,
  PurchaseResponse,
  InventoryResponse,
} from "../models/product.model"

interface PaginationParams {
  page: number
  pageSize: number
}

@Injectable({
  providedIn: "root",
})
export class ProductService {
  private apiUrl = "http://localhost:8081/api/v1/productos"
  private inventoryUrl = "http://localhost:8082/api/v1/inventarios"
  private apiKey = "secret-key-productos"
  private inventoryApiKey = "secret-key-inventario"

  private paginationParams = signal<PaginationParams>({ page: 1, pageSize: 10 })
  private productsCache = signal<ProductResponse | null>(null)
  private productDetailsCache = signal<Map<string, Product>>(new Map())
  private inventoryCache = signal<Map<number, number>>(new Map())
  private isLoading = signal(false)

  readonly products = computed(() => this.productsCache())
  readonly loading = computed(() => this.isLoading())

  constructor(private http: HttpClient) {
    
    effect(() => {
      const params = this.paginationParams()
      console.log("Parámetros de paginación actualizados:", params)
    })
  }

  private getHttpHeaders() {
    return {
      "X-API-Key": this.apiKey,
    }
  }

  private getInventoryHeaders() {
    return {
      "X-API-Key": this.inventoryApiKey,
    }
  }

  getProducts(page: number = 1, pageSize: number = 10): Observable<ProductResponse> {
    
    this.paginationParams.set({ page, pageSize })
    this.isLoading.set(true)

    let params = new HttpParams().set("page", page.toString()).set("pageSize", pageSize.toString())

    const products$ = this.http.get<ProductResponse>(this.apiUrl, {
      params,
      headers: this.getHttpHeaders(),
    })

    products$.subscribe({
      next: (response) => {
        this.productsCache.set(response)
        this.isLoading.set(false)
      },
      error: () => {
        this.isLoading.set(false)
      },
    })

    return products$
  }

  getProductById(id: number): Observable<Product> {
    console.log("[ProductService] Obteniendo producto:", id)
    const cacheMap = this.productDetailsCache()
    const cacheKey = id.toString()

    if (cacheMap.has(cacheKey)) {
      console.log("[ProductService] Producto encontrado en caché:", id)
      return new Observable((observer) => {
        observer.next(cacheMap.get(cacheKey)!)
        observer.complete()
      })
    }

    console.log("[ProductService] Solicitando producto al backend:", id)
    this.isLoading.set(true)
    const product$ = this.http
      .get<ProductDetailResponse>(`${this.apiUrl}/${id}`, {
        headers: this.getHttpHeaders(),
      })
      .pipe(
        map((response) => {
          console.log("[ProductService] Respuesta recibida:", response)
          
          const product = response.data
          
          const newCache = new Map(cacheMap)
          newCache.set(cacheKey, product)
          this.productDetailsCache.set(newCache)
          this.isLoading.set(false)
          return product
        })
      )

    return product$
  }

  getProductInventory(productId: number): Observable<{ quantity: number }> {
    
    const cache = this.inventoryCache()
    if (cache.has(productId)) {
      const cachedQuantity = cache.get(productId) || 0
      return new Observable((observer) => {
        observer.next({ quantity: cachedQuantity })
        observer.complete()
      })
    }

    return this.http
      .get<InventoryResponse>(this.inventoryUrl, {
        headers: this.getInventoryHeaders(),
      })
      .pipe(
        
        map((response) => {
          
          const newCache = new Map(cache)
          response.data.forEach((inv) => {
            newCache.set(inv.productoId, inv.cantidad)
          })
          this.inventoryCache.set(newCache)

          const inventory = response.data.find((inv) => inv.productoId === productId)
          if (inventory) {
            return { quantity: inventory.cantidad }
          } else {
            
            return { quantity: 0 }
          }
        })
      )
  }

  purchaseProduct(request: PurchaseRequest): Observable<PurchaseResponse> {
    console.log("[ProductService] Realizando compra:", request)
    this.isLoading.set(true)

    const url = `${this.inventoryUrl}/compra/${request.productId}?cantidad=${request.quantity}`

    const purchase$ = this.http
      .post<any>(
        url,
        {},
        {
          headers: this.getInventoryHeaders(),
        }
      )
      .pipe(
        map((response) => {
          console.log("[ProductService] Respuesta de compra:", response)
          
          const inventarioData = response.data
          console.log("[ProductService] Datos de inventario en respuesta:", inventarioData)
          const remainingQuantity = inventarioData?.cantidad ?? 0
          console.log("[ProductService] Cantidad restante calculada:", remainingQuantity)
          return {
            success: true,
            message: "Compra realizada exitosamente",
            remainingQuantity: remainingQuantity,
            productId: request.productId,
          }
        })
      )

    purchase$.subscribe({
      next: () => {
        
        this.productDetailsCache.set(new Map())
        this.inventoryCache.set(new Map())
        this.isLoading.set(false)
      },
      error: (error) => {
        console.error("[ProductService] Error en compra:", error)
        this.isLoading.set(false)
      },
    })

    return purchase$
  }

  clearCache(): void {
    this.productsCache.set(null)
    this.productDetailsCache.set(new Map())
    this.inventoryCache.set(new Map())
  }

  getPaginationParams(): PaginationParams {
    return this.paginationParams()
  }

  updatePagination(page: number, pageSize: number): void {
    this.paginationParams.set({ page, pageSize })
  }
}
