import { Component, Input, ChangeDetectionStrategy } from "@angular/core"
import { CommonModule } from "@angular/common"

@Component({
  selector: "app-error-display",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./error-display.component.html",
  styleUrls: ["./error-display.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ErrorDisplayComponent {
  @Input() error: string | null = null
  @Input() dismissible = true

  dismissError(): void {
    this.error = null
  }
}
