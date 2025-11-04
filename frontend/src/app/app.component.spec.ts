import { ComponentFixture, TestBed } from "@angular/core/testing"
import { RouterTestingModule } from "@angular/router/testing"
import { AppComponent } from "./app.component"

describe("ComponentoPrincipal", () => {
  let component: AppComponent
  let fixture: ComponentFixture<AppComponent>

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent, RouterTestingModule],
    }).compileComponents()

    fixture = TestBed.createComponent(AppComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it("debe crear la aplicación", () => {
    expect(component).toBeTruthy()
  })

  it("debe renderizar título", () => {
    const compiled = fixture.nativeElement
    expect(compiled.querySelector("h1").textContent).toContain("eCommerce")
  })

  it("debe tener enlace de navegación a productos", () => {
    const compiled = fixture.nativeElement
    const link = compiled.querySelector('a[routerLink="/products"]')
    expect(link).toBeTruthy()
  })
})
