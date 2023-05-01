import { JwtHelperService } from '@auth0/angular-jwt';
import { JsonObject, JsonProperty } from 'json2typescript';
import { Entity } from './entity';
import { OAuth2AccessToken } from './OAuth2AccessToken';
import { Role } from './Role';

/**
 * Class that strores info about registered user
 * @author Alexander Zhulinsky
 * @version 2.0 26 Apr 2023
 */
@JsonObject('User')
export class User extends Entity {
  @JsonProperty('firstName', String)
  private _firstName: String;
  @JsonProperty('lastName', String)
  private _lastName: String;
  @JsonProperty('login', String)
  private _login: String;
  @JsonProperty('email', String)
  private _email: String;
  @JsonProperty('password', String)
  private _password: String;
  @JsonProperty('roles', [Role])
  private _userRoles: Role[] = new Array;
  
  constructor(private _tokens?: OAuth2AccessToken) {
    super();
    if (_tokens && _tokens.access_token) {
      const accessToken = (new JwtHelperService).decodeToken(_tokens.access_token.toString());
      this.id = accessToken['id'];
      this._firstName = accessToken['firstName'];
      this._lastName = accessToken['lastName'];
      this._login = accessToken['login'];
      this._email = accessToken['email'];
      this._password = accessToken['password'];
      const roles = accessToken['roles'];
      if (roles) {
        roles.forEach((el: { name: string; description: string; }) => {
          const role = new Role(el.name, el.description);
          this.userRoles.push(role);
      });
      } 
    } else {
      this.id = 0;
      this._firstName = '';
      this._lastName = '';
      this._login = 'annon';
      this._email = '';
      this._password = '';
      this._userRoles = new Array;
    }
  }

  public get email(): String {
    return this._email;
  }
  public set email(value: String) {
    this._email = value;
  }

	public get firstName(): String {
		return this._firstName;
	}

	public set firstName(value: String) {
		this._firstName = value;
	}

	public get lastName(): String {
		return this._lastName;
	}

	public set lastName(value: String) {
		this._lastName = value;
	}

	public get userRoles(): Role[] {
		return this._userRoles;
	}

	public set userRoles(value: Role[]) {
		this._userRoles = value;
	}

  public get password(): String {
    return this._password;
  }
  public set password(value: String) {
    this._password = value;
  }
  public get login(): String {
    return this._login;
  }
  public set login(value: String) {
    this._login = value;
  }

  public get tokens(): OAuth2AccessToken | undefined {
    return this._tokens;
  }

  toJsonString(): string {
    return JSON.stringify(this);
}

  public isEmpty(): boolean {
    throw new Error('Method not implemented.');
  }
  
 }
