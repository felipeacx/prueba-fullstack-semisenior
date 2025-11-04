import { TestBed } from "@angular/core/testing"
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing"
import { ProductService } from "./product.service"
import {
  ProductResponse,
  ProductDetailResponse,
  Product,
  InventoryResponse,
  PurchaseResponse,
} from "../models/product.model"

describe("ServicioProducto", () => {
  let service: ProductService
  let httpMock: HttpTestingController
  const productsUrl = "http://localhost:8081/api/v1/productos"
  const inventoryUrl = "http://localhost:8082/api/v1/inventarios"

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProductService],
    })
    service = TestBed.inject(ProductService)
    httpMock = TestBed.inject(HttpTestingController)
  })

  afterEach(() => {
    httpMock.verify()
  })

  it("debe ser creado", () => {
    expect(service).toBeTruthy()
  })

  describe("obtenerProductos", () => {
    it("debe obtener productos con paginación usando URL correcta", () => {
      const mockResponse: ProductResponse = {
        data: [
          {
            id: 1,
            codigo: "PROD001",
            nombre: "Producto 1",
            descripcion: "Desc 1",
            precio: 100,
            categoria: "Electrónica",
          },
          {
            id: 2,
            codigo: "PROD002",
            nombre: "Producto 2",
            descripcion: "Desc 2",
            precio: 200,
            categoria: "Informática",
          },
        ],
        meta: {
          total: 2,
          pages: 1,
          current_page: 1,
          page_size: 10,
        },
      }

      service.getProducts(1, 10).subscribe((response) => {
        expect(response.data.length).toBe(2)
        expect(response.meta?.total).toBe(2)
        expect(response.data[0].codigo).toBe("PROD001")
      })

      const reqs = httpMock.match(
        (request) =>
          request.url === productsUrl &&
          request.params.get("page") === "1" &&
          request.params.get("pageSize") === "10"
      )
      expect(reqs.length).toBe(2)
      reqs.forEach((req) => {
        expect(req.request.method).toBe("GET")
        expect(req.request.headers.get("X-API-Key")).toBe("secret-key-productos")
        req.flush(mockResponse)
      })
    })

    it("debe incluir encabezado de Clave API", () => {
      const mockResponse: ProductResponse = {
        data: [],
        meta: { total: 0, pages: 0, current_page: 1, page_size: 10 },
      }

      service.getProducts(1, 10).subscribe()

      const reqs = httpMock.match((request) => request.url === productsUrl)
      expect(reqs.length).toBe(2)
      reqs.forEach((req) => {
        expect(req.request.headers.get("X-API-Key")).toBe("secret-key-productos")
        req.flush(mockResponse)
      })
    })
  })

  describe("obtenerProductoPorId", () => {
    it("debe obtener detalle del producto por id", () => {
      const mockProduct: Product = {
        id: 1,
        codigo: "LAPTOP001",
        nombre: "Laptop Dell XPS 15",
        descripcion: "Laptop de alta performance",
        precio: 5999990,
        categoria: "Electrónica",
      }

      const mockResponse: ProductDetailResponse = {
        data: mockProduct,
        links: { self: "/api/v1/productos/1", first: null, last: null, next: null, prev: null },
      }

      service.getProductById(1).subscribe((product) => {
        expect(product).toEqual(mockProduct)
        expect(product.id).toBe(1)
        expect(product.codigo).toBe("LAPTOP001")
      })

      const reqs = httpMock.match(`${productsUrl}/1`)
      expect(reqs.length).toBeGreaterThan(0)
      reqs.forEach((req) => {
        expect(req.request.method).toBe("GET")
        expect(req.request.headers.get("X-API-Key")).toBe("secret-key-productos")
        req.flush(mockResponse)
      })
    })

    it("debe guardar en caché productos después de la primera solicitud", () => {
      const mockProduct: Product = {
        id: 1,
        codigo: "PROD001",
        nombre: "Producto 1",
        descripcion: "Desc 1",
        precio: 100,
        categoria: "Cat1",
      }

      const mockResponse: ProductDetailResponse = {
        data: mockProduct,
        links: { self: "/api/v1/productos/1", first: null, last: null, next: null, prev: null },
      }

      service.getProductById(1).subscribe((product) => {
        expect(product.id).toBe(1)
      })

      const reqs1 = httpMock.match(`${productsUrl}/1`)
      reqs1.forEach((req) => req.flush(mockResponse))

      service.getProductById(1).subscribe((product) => {
        expect(product.id).toBe(1)
      })

      const reqs2 = httpMock.match(`${productsUrl}/1`)
      expect(reqs2.length).toBe(0)
    })
  })

  describe("obtenerInventarioProducto", () => {
    it("debe obtener inventario del producto desde servicio de inventario", () => {
      const mockInventoryData = {
        data: [
          { id: 1, productoId: 1, cantidad: 50, cantidadMinima: 10 },
          { id: 2, productoId: 2, cantidad: 30, cantidadMinima: 5 },
        ],
      }

      service.getProductInventory(1).subscribe((inventory) => {
        expect(inventory.quantity).toBe(50)
      })

      const reqs = httpMock.match((request) => request.url === inventoryUrl)
      expect(reqs.length).toBeGreaterThan(0)
      reqs.forEach((req) => {
        expect(req.request.method).toBe("GET")
        expect(req.request.headers.get("X-API-Key")).toBe("secret-key-inventario")
        req.flush(mockInventoryData)
      })
    })

    it("debe devolver cantidad 0 si producto no se encuentra en inventario", () => {
      const mockInventoryData = {
        data: [{ id: 1, productoId: 2, cantidad: 30, cantidadMinima: 5 }],
      }

      service.getProductInventory(999).subscribe((inventory) => {
        expect(inventory.quantity).toBe(0)
      })

      const reqs = httpMock.match((request) => request.url === inventoryUrl)
      reqs.forEach((req) => req.flush(mockInventoryData))
    })

    it("debe usar caché de inventario en segunda solicitud", () => {
      const mockInventoryData = {
        data: [{ id: 1, productoId: 1, cantidad: 50, cantidadMinima: 10 }],
      }

      service.getProductInventory(1).subscribe((inventory) => {
        expect(inventory.quantity).toBe(50)
      })

      const reqs1 = httpMock.match((request) => request.url === inventoryUrl)
      reqs1.forEach((req) => req.flush(mockInventoryData))

      service.getProductInventory(1).subscribe((inventory) => {
        expect(inventory.quantity).toBe(50)
      })

      const reqs2 = httpMock.match((request) => request.url === inventoryUrl)
      expect(reqs2.length).toBe(0)
    })
  })

  describe("comprarProducto", () => {
    it("debe comprar un producto exitosamente", () => {
      const purchaseRequest = { productId: 1, quantity: 5 }
      const mockInventoryResponse = {
        data: { id: 1, productoId: 1, cantidad: 45, cantidadMinima: 10 },
      }

      service.purchaseProduct(purchaseRequest).subscribe((response) => {
        expect(response.success).toBe(true)
        expect(response.remainingQuantity).toBe(45)
        expect(response.productId).toBe(1)
      })

      const reqs = httpMock.match(
        (request) =>
          request.url === `${inventoryUrl}/compra/1?cantidad=5` && request.method === "POST"
      )
      expect(reqs.length).toBeGreaterThan(0)
      reqs.forEach((req) => {
        expect(req.request.headers.get("X-API-Key")).toBe("secret-key-inventario")
        req.flush(mockInventoryResponse)
      })
    })

    it("debe manejar error de compra correctamente", () => {
      const purchaseRequest = { productId: 1, quantity: 100 }
      const errorResponse = { message: "Insufficient inventory" }

      service.purchaseProduct(purchaseRequest).subscribe(
        () => fail("should have failed"),
        (error) => {
          expect(error.status).toBe(409)
        }
      )

      const reqs = httpMock.match(
        (request) => request.url === `${inventoryUrl}/compra/1?cantidad=100`
      )
      expect(reqs.length).toBeGreaterThan(0)
      reqs.forEach((req) => {
        req.flush(errorResponse, { status: 409, statusText: "Conflict" })
      })
    })

    it("debe limpiar caché después de compra exitosa", () => {
      const purchaseRequest = { productId: 1, quantity: 1 }
      const mockInventoryResponse = {
        data: { id: 1, productoId: 1, cantidad: 49, cantidadMinima: 10 },
      }

      service.purchaseProduct(purchaseRequest).subscribe((response) => {
        expect(response.success).toBe(true)
      })

      const reqs = httpMock.match((request) => request.url.includes("/compra/1"))
      expect(reqs.length).toBeGreaterThan(0)
      reqs.forEach((req) => req.flush(mockInventoryResponse))
    })
  })

  describe("limpiarCache", () => {
    it("debe limpiar todos los cachés", () => {
      service.clearCache()

      const mockProduct: ProductDetailResponse = {
        data: { id: 1, codigo: "P1", nombre: "P", descripcion: "D", precio: 100, categoria: "C" },
        links: { self: "/", first: null, last: null, next: null, prev: null },
      }

      service.getProductById(1).subscribe()
      const reqs = httpMock.match(`${productsUrl}/1`)
      expect(reqs.length).toBeGreaterThan(0)
      reqs.forEach((req) => req.flush(mockProduct))
    })
  })
})
