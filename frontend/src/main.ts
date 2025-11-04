import { bootstrapApplication } from "@angular/platform-browser"
import { provideRouter } from "@angular/router"
import { provideHttpClient, withInterceptors, HTTP_INTERCEPTORS } from "@angular/common/http"
import { provideAnimations } from "@angular/platform-browser/animations"
import { LOCALE_ID } from "@angular/core"
import { registerLocaleData } from "@angular/common"
import localeEs from "@angular/common/locales/es"
import { AppComponent } from "./app/app.component"
import { routes } from "./app/app.routes"
import { ErrorInterceptor } from "./app/interceptors/error.interceptor"

registerLocaleData(localeEs)

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    provideAnimations(),
    {
      provide: LOCALE_ID,
      useValue: "es",
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorInterceptor,
      multi: true,
    },
  ],
}).catch((err) => console.error(err))
