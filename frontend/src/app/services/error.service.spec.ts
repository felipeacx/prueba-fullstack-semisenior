import { TestBed } from "@angular/core/testing"
import { ErrorService } from "./error.service"

describe("ServicioError", () => {
  let service: ErrorService

  beforeEach(() => {
    TestBed.configureTestingModule({})
    service = TestBed.inject(ErrorService)
  })

  it("debe ser creado", () => {
    expect(service).toBeTruthy()
  })

  describe("gestión de errores con Signals", () => {
    it("debe establecer y obtener error", () => {
      service.setError("Test error")
      expect(service.getError()).toBe("Test error")
    })

    it("debe limpiar error", () => {
      service.setError("Test error")
      service.clearError()
      expect(service.getError()).toBeNull()
    })

    it("debe obtener valor de error actual", () => {
      service.setError("Current error")
      expect(service.getError()).toBe("Current error")
    })

    it("debe devolver null cuando no hay error establecido", () => {
      service.clearError()
      expect(service.getError()).toBeNull()
    })
  })

  describe("gestión de carga con Signals", () => {
    it("debe establecer y obtener estado de carga", () => {
      service.setLoading(true)
      expect(service.isLoading()).toBe(true)
    })

    it("debe limpiar estado de carga", () => {
      service.setLoading(true)
      service.setLoading(false)
      expect(service.isLoading()).toBe(false)
    })

    it("debe verificar si está cargando", () => {
      service.setLoading(true)
      expect(service.isLoading()).toBe(true)

      service.setLoading(false)
      expect(service.isLoading()).toBe(false)
    })

    it("debe tener carga por defecto en false", () => {
      expect(service.isLoading()).toBe(false)
    })
  })

  describe("error and loading interaction", () => {
    it("should handle error and loading independently", () => {
      service.setLoading(true)
      service.setError("Some error")

      expect(service.isLoading()).toBe(true)
      expect(service.getError()).toBe("Some error")

      service.setLoading(false)
      expect(service.isLoading()).toBe(false)
      expect(service.getError()).toBe("Some error")

      service.clearError()
      expect(service.getError()).toBeNull()
      expect(service.isLoading()).toBe(false)
    })
  })
})
