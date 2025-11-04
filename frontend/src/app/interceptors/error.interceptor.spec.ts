import { TestBed } from "@angular/core/testing"
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing"
import { HTTP_INTERCEPTORS, HttpClient, HttpErrorResponse } from "@angular/common/http"
import { ErrorInterceptor } from "./error.interceptor"
import { ErrorService } from "../services/error.service"

describe("InterceptorDeError", () => {
  let httpClient: HttpClient
  let httpMock: HttpTestingController
  let errorService: ErrorService

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ErrorService,
        { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
      ],
    })

    httpClient = TestBed.inject(HttpClient)
    httpMock = TestBed.inject(HttpTestingController)
    errorService = TestBed.inject(ErrorService)
  })

  afterEach(() => {
    httpMock.verify()
  })

  it("debe ser creado", () => {
    const interceptor = TestBed.inject(HTTP_INTERCEPTORS).find((i) => i instanceof ErrorInterceptor)
    expect(interceptor).toBeTruthy()
  })

  it("debe establecer estado de carga durante la solicitud", () => {
    let testComplete = false

    httpClient.get("/test").subscribe(
      () => {
        testComplete = true
      },
      () => {
        testComplete = true
      }
    )

    expect(errorService.isLoading()).toBe(true)

    const req = httpMock.expectOne("/test")
    req.flush({ data: "test" })

    expect(errorService.isLoading()).toBe(false)
    expect(testComplete).toBe(true)
  })

  it("debe manejar respuestas de error HTTP", () => {
    let errorOccurred = false

    httpClient.get("/test").subscribe(
      () => {
        fail("should have failed")
      },
      (error: HttpErrorResponse) => {
        errorOccurred = true
      }
    )

    const req = httpMock.expectOne("/test")
    req.flush({ message: "Server error" }, { status: 500, statusText: "Server Error" })

    expect(errorOccurred).toBe(true)
    expect(errorService.getError()).toBeTruthy()
    expect(errorService.isLoading()).toBe(false)
  })

  it("debe manejar errores del lado del cliente", () => {
    let errorReceived = false

    httpClient.get("/test").subscribe(
      () => fail("should have failed"),
      (error: HttpErrorResponse) => {
        errorReceived = true
      }
    )

    const req = httpMock.expectOne("/test")
    req.error(new ProgressEvent("error"))

    expect(errorReceived).toBe(true)
    expect(errorService.getError()).toBeTruthy()
  })

  it("debe limpiar error después de solicitud exitosa", () => {
    
    errorService.setError("Previous error")
    expect(errorService.getError()).toBe("Previous error")

    let successReceived = false
    httpClient.get("/test").subscribe(() => {
      successReceived = true
    })

    const req = httpMock.expectOne("/test")
    req.flush({ data: "success" })

    expect(successReceived).toBe(true)
    expect(errorService.isLoading()).toBe(false)
  })
})
