import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-change-password-confirmation-dialog',
  imports: [MatButtonModule, MatDialogModule, MatIconModule],
  templateUrl: './change-password-confirmation-dialog.html',
  styleUrl: './change-password-confirmation-dialog.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangePasswordConfirmationDialog {
  private readonly dialogRef = inject(MatDialogRef<ChangePasswordConfirmationDialog, boolean>);

  protected cancel(): void {
    this.dialogRef.close(false);
  }

  protected confirm(): void {
    this.dialogRef.close(true);
  }
}
