import { Injectable } from "@angular/core"
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse,
} from "@angular/common/http"
import { Observable, throwError } from "rxjs"
import { catchError, finalize } from "rxjs/operators"
import { ErrorService } from "../services/error.service"

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private errorService: ErrorService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    this.errorService.setLoading(true)

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = "An error occurred"

        if (error.error instanceof ErrorEvent) {
          
          errorMessage = `Error: ${error.error.message}`
        } else {
          
          errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`
          if (error.error && error.error.message) {
            errorMessage = error.error.message
          }
        }

        console.error("HTTP Error:", errorMessage)
        this.errorService.setError(errorMessage)
        return throwError(() => new Error(errorMessage))
      }),
      finalize(() => {
        this.errorService.setLoading(false)
      })
    )
  }
}
