import { ComponentFixture, TestBed } from "@angular/core/testing"
import { HttpClientTestingModule } from "@angular/common/http/testing"
import { RouterTestingModule } from "@angular/router/testing"
import { ActivatedRoute } from "@angular/router"
import { of } from "rxjs"
import { ProductDetailComponent } from "./product-detail.component"
import { ProductService } from "../../services/product.service"
import { ErrorService } from "../../services/error.service"
import { Product } from "../../models/product.model"

describe("ComponentoDetalleProducto", () => {
  let component: ProductDetailComponent
  let fixture: ComponentFixture<ProductDetailComponent>
  let productService: ProductService
  let errorService: ErrorService

  const mockProduct: Product = {
    id: 1,
    codigo: "LAPTOP001",
    nombre: "Laptop Dell XPS 15",
    descripcion: "Laptop de alta performance",
    precio: 5999990,
    categoria: "Electrónica",
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductDetailComponent, HttpClientTestingModule, RouterTestingModule],
      providers: [
        ProductService,
        ErrorService,
        {
          provide: ActivatedRoute,
          useValue: {
            params: of({ id: "1" }),
          },
        },
      ],
    }).compileComponents()

    fixture = TestBed.createComponent(ProductDetailComponent)
    component = fixture.componentInstance
    productService = TestBed.inject(ProductService)
    errorService = TestBed.inject(ErrorService)

    spyOn(productService, "getProductById").and.returnValue(of(mockProduct))
    spyOn(productService, "getProductInventory").and.returnValue(of({ quantity: 50 }))
    spyOn(productService, "purchaseProduct").and.returnValue(
      of({
        success: true,
        message: "Compra realizada exitosamente",
        remainingQuantity: 49,
        productId: 1,
      })
    )
  })

  it("debe ser creado", () => {
    expect(component).toBeTruthy()
  })

  it("debe cargar producto al iniciar", (done) => {
    fixture.detectChanges()

    setTimeout(() => {
      expect(component.product()).toEqual(mockProduct)
      expect(component.loading()).toBe(false)
      done()
    }, 100)
  })

  it("debe cargar inventario al iniciar", (done) => {
    fixture.detectChanges()

    setTimeout(() => {
      expect(component.inventory()).toBe(50)
      done()
    }, 100)
  })

  it("debe aumentar cantidad con Signals", () => {
    component.inventory.set(10)
    component.quantity.set(1)

    component.increaseQuantity()

    expect(component.quantity()).toBe(2)
  })

  it("no debe aumentar cantidad más allá del inventario", () => {
    component.inventory.set(5)
    component.quantity.set(5)

    component.increaseQuantity()

    expect(component.quantity()).toBe(5)
  })

  it("debe disminuir cantidad con Signals", () => {
    component.quantity.set(5)

    component.decreaseQuantity()

    expect(component.quantity()).toBe(4)
  })

  it("no debe disminuir cantidad por debajo de 1", () => {
    component.quantity.set(1)

    component.decreaseQuantity()

    expect(component.quantity()).toBe(1)
  })

  it("debe validar cantidad de compra", () => {
    component.product.set(mockProduct)
    component.quantity.set(0)
    component.inventory.set(50)

    component.purchaseProduct()

    expect(component.error()).toBe("Invalid quantity")
  })

  it("debe validar que cantidad de compra no exceda inventario", () => {
    component.product.set(mockProduct)
    component.quantity.set(60)
    component.inventory.set(50)

    component.purchaseProduct()

    expect(component.error()).toBe("Invalid quantity")
  })

  it("debe llamar servicio de compra en validación exitosa", () => {
    component.product.set(mockProduct)
    component.quantity.set(5)
    component.inventory.set(50)

    component.purchaseProduct()

    expect(productService.purchaseProduct).toHaveBeenCalledWith({
      productId: 1,
      quantity: 5,
    })
  })

  it("debe manejar éxito de compra", (done) => {
    component.product.set(mockProduct)
    component.quantity.set(1)
    component.inventory.set(50)

    component.purchaseProduct()

    setTimeout(() => {
      expect(component.purchaseSuccess()).toBe(true)
      expect(component.inventory()).toBe(49)
      done()
    }, 100)
  })

  it("debe establecer estado de carga durante compra", (done) => {
    component.product.set(mockProduct)
    component.quantity.set(1)
    component.inventory.set(50)

    expect(component.purchasing()).toBe(false)

    component.purchaseProduct()

    setTimeout(() => {
      
      expect(component.purchasing()).toBe(false)
      expect(component.purchaseSuccess()).toBe(true)
      done()
    }, 100)
  })

  it("debe tener método goBack", () => {

    expect(component.goBack).toBeDefined()
    expect(typeof component.goBack).toBe("function")
  })
})
