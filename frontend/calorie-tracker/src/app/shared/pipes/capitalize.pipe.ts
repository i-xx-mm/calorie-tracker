import { Pipe, PipeTransform } from '@angular/core';

/**
 * Angular pure pipe to capitalize the first character of input string.
 * Returns empty string for null/undefined/non-string input.
 */
@Pipe({
  name: 'capitalize'
})
export class CapitalizePipe implements PipeTransform {
  /**
   * Capitalize first letter, keep remaining characters unchanged
   * 
   * @param value raw input string to transform
   * @returns string with first character uppercase; empty string for invalid input
   */
  transform(value: string): string {
    if (!value || typeof value !== 'string') return '';
    return value.charAt(0).toUpperCase() + value.slice(1);
  }
}