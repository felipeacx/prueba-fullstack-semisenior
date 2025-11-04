export interface Product {
  id: number
  codigo: string
  nombre: string
  descripcion: string
  precio: number
  categoria?: string
  createdAt?: Date
  updatedAt?: Date
}

export interface ProductResponse {
  data: Product[]
  meta?: {
    total: number
    pages: number
    current_page: number
    page_size: number
  }
}

export interface ProductDetailResponse {
  data: Product
  links?: {
    self: string
    first?: string | null
    last?: string | null
    next?: string | null
    prev?: string | null
  }
}

export interface Inventory {
  productId: number
  quantity: number
  lastUpdated?: Date
}

export interface InventoryRecord {
  id: number
  productoId: number
  cantidad: number
  cantidadMinima: number
}

export interface InventoryResponse {
  data: InventoryRecord[]
  meta?: {
    total: number
    pages: number
    current_page: number
    page_size: number
  }
}

export interface PurchaseResponse {
  success: boolean
  message: string
  remainingQuantity?: number
  productId?: number
}

export interface PurchaseRequest {
  productId: number
  quantity: number
}
