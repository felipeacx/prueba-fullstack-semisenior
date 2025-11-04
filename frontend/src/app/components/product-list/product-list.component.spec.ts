import { ComponentFixture, TestBed } from "@angular/core/testing"
import { HttpClientTestingModule } from "@angular/common/http/testing"
import { RouterTestingModule } from "@angular/router/testing"
import { ProductListComponent } from "./product-list.component"
import { ProductService } from "../../services/product.service"
import { ErrorService } from "../../services/error.service"
import { of, throwError } from "rxjs"
import { ProductResponse, Product } from "../../models/product.model"

describe("ComponentoListaProductos", () => {
  let component: ProductListComponent
  let fixture: ComponentFixture<ProductListComponent>
  let productService: ProductService
  let errorService: ErrorService

  const mockProducts: Product[] = [
    {
      id: 1,
      codigo: "PROD001",
      nombre: "Producto 1",
      descripcion: "Desc 1",
      precio: 100,
      categoria: "Cat1",
    },
    {
      id: 2,
      codigo: "PROD002",
      nombre: "Producto 2",
      descripcion: "Desc 2",
      precio: 200,
      categoria: "Cat2",
    },
  ]

  const mockResponse: ProductResponse = {
    data: mockProducts,
    meta: {
      total: 2,
      pages: 1,
      current_page: 1,
      page_size: 10,
    },
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductListComponent, HttpClientTestingModule, RouterTestingModule],
      providers: [ProductService, ErrorService],
    }).compileComponents()

    fixture = TestBed.createComponent(ProductListComponent)
    component = fixture.componentInstance
    productService = TestBed.inject(ProductService)
    errorService = TestBed.inject(ErrorService)

    spyOn(productService, "getProducts").and.returnValue(of(mockResponse))
  })

  it("debe ser creado", () => {
    expect(component).toBeTruthy()
  })

  it("debe cargar productos al iniciar", (done) => {
    fixture.detectChanges()

    setTimeout(() => {
      expect(component.products()).toEqual(mockProducts)
      expect(component.loading()).toBe(false)
      done()
    }, 100)
  })

  it("debe inicializar con página indexada en 0", () => {
    expect(component.currentPage()).toBe(0)
  })

  it("debe mostrar todos los productos de la respuesta", (done) => {
    fixture.detectChanges()

    setTimeout(() => {
      expect(component.products().length).toBe(2)
      expect(component.products()[0].codigo).toBe("PROD001")
      expect(component.products()[1].codigo).toBe("PROD002")
      done()
    }, 100)
  })

  it("debe navegar a página siguiente con Signals", (done) => {
    
    const mockResponse: ProductResponse = {
      data: [],
      meta: {
        total: 2,
        pages: 2,
        current_page: 2,
        page_size: 10,
      },
    }
    ;(productService.getProducts as jasmine.Spy).and.returnValue(of(mockResponse))

    component.currentPage.set(0)
    component.totalPages.set(2)

    component.nextPage()

    setTimeout(() => {
      
      expect(component.currentPage()).toBe(1)
      expect(productService.getProducts).toHaveBeenCalled()
      done()
    }, 50)
  })

  it("no debe navegar más allá de la última página", () => {
    component.currentPage.set(0)
    component.totalPages.set(1)

    const initialPage = component.currentPage()
    component.nextPage()

    expect(component.currentPage()).toBe(initialPage)
  })

  it("debe navegar a página anterior con Signals", () => {
    component.currentPage.set(1)

    component.previousPage()

    expect(component.currentPage()).toBe(0)
    expect(productService.getProducts).toHaveBeenCalled()
  })

  it("no debe navegar a página negativa", () => {
    component.currentPage.set(0)

    component.previousPage()

    expect(component.currentPage()).toBe(0)
  })

  it("debe establecer estado de carga mientras se obtienen datos", (done) => {

    component.loadProducts()

    setTimeout(() => {
      
      expect(component.loading()).toBe(false)
      expect(productService.getProducts).toHaveBeenCalled()
      done()
    }, 50)
  })

  it("debe manejar errores del servicio", (done) => {
    const errorMessage = "Failed to load products"
    ;(productService.getProducts as jasmine.Spy).and.returnValue(
      throwError(() => new Error(errorMessage))
    )

    component.loadProducts()

    setTimeout(() => {
      expect(component.error()).toBe("Error al cargar productos. Por favor intenta de nuevo.")
      expect(component.loading()).toBe(false)
      done()
    }, 100)
  })

  it("debe actualizar totalPages desde meta", (done) => {
    fixture.detectChanges()

    setTimeout(() => {
      expect(component.totalPages()).toBe(1)
      done()
    }, 100)
  })

  it("debe establecer tamaño de página correctamente", () => {
    expect(component.pageSize).toBe(10)
  })

  it("debe limpiar error cuando carga es exitosa", (done) => {
    component.error.set("Previous error")

    component.loadProducts()

    setTimeout(() => {
      expect(component.error()).toBe(null)
      done()
    }, 100)
  })

  it("debe enviar número de página correcto al servicio (indexado en 1)", () => {
    component.currentPage.set(0)
    component.loadProducts()

    expect(productService.getProducts).toHaveBeenCalledWith(1, 10)
  })

  it("debe ver detalles del producto", () => {
    
    expect(component.viewDetails).toBeDefined()
  })
})
