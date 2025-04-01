import { AbstractControl } from "@angular/forms";

export function getErrorMessages(control: AbstractControl | null, validaciones: any): string[] {
    if (!control) return [];

    return validaciones?.filter((error: { type: string, message: string }) =>
        control.hasError(error.type) && (control.dirty || control.touched)
    ).map((error: { message: string }) => error.message) || [];
}
