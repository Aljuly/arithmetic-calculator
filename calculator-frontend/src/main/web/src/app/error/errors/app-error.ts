/**
 * Generic application error.
 */
export class AppError extends Error {

  constructor(public message: string, public cause?: any) {
    super(message);

    // Set the prototype explicitly.
    // https://github.com/Microsoft/TypeScript-wiki/blob/master/
    // Breaking-Changes.md#extending-built-ins-like-error-array-and-map-may-no-longer-work
    Object.setPrototypeOf(this, AppError.prototype);
  }
}
