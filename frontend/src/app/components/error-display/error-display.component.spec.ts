import { ComponentFixture, TestBed } from "@angular/core/testing"
import { ErrorDisplayComponent } from "./error-display.component"
import { DebugElement } from "@angular/core"
import { By } from "@angular/platform-browser"

describe("ComponentoMostrarError", () => {
  let component: ErrorDisplayComponent
  let fixture: ComponentFixture<ErrorDisplayComponent>

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ErrorDisplayComponent],
    }).compileComponents()

    fixture = TestBed.createComponent(ErrorDisplayComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it("debe ser creado", () => {
    expect(component).toBeTruthy()
  })

  it("debe tener propiedad dismissible con defecto verdadero", () => {
    expect(component.dismissible).toBe(true)
  })

  it("debe inicializar con error nulo", () => {
    expect(component.error).toBeNull()
  })

  it("debe poder establecer error", () => {
    component.error = "Test error"
    expect(component.error).toBe("Test error")
  })

  it("debe descartar error", () => {
    component.error = "Test error"
    component.dismissible = true

    component.dismissError()

    expect(component.error).toBeNull()
  })

  it("no debe mostrar nada cuando error es nulo", () => {
    component.error = null
    fixture.detectChanges()

    const errorDisplay = fixture.nativeElement.querySelector(".error-display")
    expect(errorDisplay).toBeFalsy()
  })

  it("debe aceptar entrada dismissible", () => {
    component.dismissible = false
    fixture.detectChanges()

    expect(component.dismissible).toBe(false)
  })
})
