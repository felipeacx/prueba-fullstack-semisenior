import { Injectable, signal, computed } from "@angular/core"

@Injectable({
  providedIn: "root",
})
export class ErrorService {

  private errorSignal = signal<string | null>(null)

  private loadingSignal = signal<boolean>(false)

  private errorHistorySignal = signal<string[]>([])

  readonly error = computed(() => this.errorSignal())

  readonly loading = computed(() => this.loadingSignal())

  readonly errorHistory = computed(() => this.errorHistorySignal())

  readonly hasError = computed(() => this.errorSignal() !== null)

  constructor() {}

  setError(error: string | null): void {
    this.errorSignal.set(error)

    if (error !== null) {
      const history = this.errorHistorySignal()
      this.errorHistorySignal.set([...history, error])
    }
  }

  getError(): string | null {
    return this.errorSignal()
  }

  clearError(): void {
    this.errorSignal.set(null)
  }

  setLoading(loading: boolean): void {
    this.loadingSignal.set(loading)
  }

  isLoading(): boolean {
    return this.loadingSignal()
  }

  clearErrorHistory(): void {
    this.errorHistorySignal.set([])
  }

  getErrorHistory(): string[] {
    return this.errorHistorySignal()
  }

  getErrorCount(): number {
    return this.errorHistorySignal().length
  }
}
