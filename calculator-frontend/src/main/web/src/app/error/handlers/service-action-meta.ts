import {HttpMethod} from './http-method';

/**
 * Representation of invocation a service method (like getUsers, updatePost, etc).
 * If the service method fails an information contained in this object will be used
 * in an error handler.
 */
export class ServiceActionMeta {
    private _action: string;
    private _method: HttpMethod;
    private _postId: number;

    static of(action: string): ServiceActionMeta {
        const instance: ServiceActionMeta = new ServiceActionMeta();
        instance._action = action;
        return instance;
    }

    withHttpMethod(method: HttpMethod): ServiceActionMeta {
        this._method = method;
        return this;
    }

    withPostId(postId: number): ServiceActionMeta {
        this._postId = postId;
        return this;
    }

    /**
     * Builds a single message string from the fields.
     * @returns {string} description of why the service method has failed
     */
    getErrorMessage(): string {
        // build generic message
        let message = `Cannot ${this.action}`;
        if (this.postId) {
            message += ` with id:${this.postId}`;
        }
        if (this.method) {
            message += `, HTTP ${this.method} was unsuccessful`;
        }
        return message;
    }

    get action(): string {
        return this._action;
    }

    get method(): HttpMethod {
        return this._method;
    }

    get postId(): number {
        return this._postId;
    }
}
