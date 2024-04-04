export class OAuth2AccessToken {

    constructor(private _access_token: String,
                private _token_type: String,
                private _refresh_token: String,
                private _expires_in: String,
                private _scope: String,
                private _organizationId: String,
                private _jti: String) {
    }

    get access_token(): String {
        return this._access_token;
    }

    set access_token(value: String) {
        this._access_token = value;
    }

    get token_type(): String {
        return this._token_type;
    }

    set token_type(value: String) {
        this._token_type = value;
    }

    get refresh_token(): String {
        return this._refresh_token;
    }

    set refresh_token(value: String) {
        this._refresh_token = value;
    }

    get expires_in(): String {
        return this._expires_in;
    }

    set expires_in(value: String) {
        this._expires_in = value;
    }

    get scope(): String {
        return this._scope;
    }

    set scope(value: String) {
        this._scope = value;
    }

    get organizationId(): String {
        return this._organizationId;
    }

    set organizationId(value: String) {
        this._organizationId = value;
    }

    get jti(): String {
        return this._jti;
    }

    set jti(value: String) {
        this._jti = value;
    }

    public static fromJson(json: String): OAuth2AccessToken {
        return new OAuth2AccessToken(
            json['access_token'],
            json['token_type'],
            json['refresh_token'],
            json['expires_in'],
            json['scope'],
            json['organizationId'],
            json['jti']);
    }
}
