import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-logout-confirmation-dialog',
  imports: [MatButtonModule, MatDialogModule, MatIconModule],
  templateUrl: './logout-confirmation-dialog.html',
  styleUrl: './logout-confirmation-dialog.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LogoutConfirmationDialog {
  private readonly dialogRef = inject(MatDialogRef<LogoutConfirmationDialog, boolean>);

  protected cancel(): void {
    this.dialogRef.close(false);
  }

  protected confirm(): void {
    this.dialogRef.close(true);
  }
}
