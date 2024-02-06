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
  @JsonProperty('avatar', String)
  private _avatar: String;
  @JsonProperty('email', String)
  private _email: String;
  @JsonProperty('password', String)
  private _password: String;
  @JsonProperty('roles', [Role])
  private _userRoles: Role[] = new Array();
  @JsonProperty('last-login', String)
  private _lastlogin: String;
  @JsonProperty('enabled', Boolean)
  private _enabled: Boolean;
  @JsonProperty('bnned', Boolean)
  private _banned: Boolean;
  @JsonProperty('verified', Boolean)
  private _verified: Boolean;
  @JsonProperty('banReasone', String)
  private _banReason: String;

  constructor(private _tokens?: OAuth2AccessToken) {
    super();
    if (_tokens && _tokens.access_token) {
      const accessToken = new JwtHelperService().decodeToken(
        _tokens.access_token.toString()
      );
      this.id = accessToken['id'];
      this._firstName = accessToken['firstName'];
      this._lastName = accessToken['lastName'];
      this._avatar = accessToken['avatar'];
      this._login = accessToken['login'];
      this._email = accessToken['email'];
      this._password = accessToken['password'];
      this._lastlogin = accessToken['last-login'];
      this._enabled = accessToken['enabled'];
      this._banned = accessToken['banned'];
      this._verified = accessToken['verified'];
      this._banReason = accessToken['banReason'];
      const roles = accessToken['userRoles'];
      if (roles) {
        roles.forEach((el: { name: string; description: string }) => {
          const role = new Role(el.name, el.description);
          this._userRoles.push(role);
        });
      }
    } else {
      this.id = 0;
      this._firstName = '';
      this._lastName = '';
      this._avatar = '';
      this._login = 'annon';
      this._email = '';
      this._password = '';
      this._lastlogin = '';
      this._enabled = false;
      this._banned = false;
      this._verified = false;
      this._banReason = '';
      this._userRoles = new Array();
    }
  }

  static fromLocaleStorage(userDto: any): User {
    const user: User = new User();
    user.id = userDto._id;
    user.firstName = userDto._firstName || '';
    user.lastName = userDto._lastName || '';
    user.avatar = userDto._avatar || '';
    user.login = userDto._login || '';
    user.email = userDto._email || '';
    user.password = userDto._password || '';
    user.lastlogin = userDto._lastlogin || '';
    user.enabled = userDto._enabled || false;
    user.banned = userDto._banned || false;
    user.verified = userDto._verified || false;
    user.banReason = userDto._banReason || '';
    const roles = userDto._userRoles;
    if (roles) {
        roles.forEach(role => {
        user._userRoles.push(new Role(role.name, role.description));
      });
    }
    return user;
  }

  public get email(): string {
    return this._email?.toString();
  }
  public set email(value: string) {
    this._email = value;
  }

  public get firstName(): string {
    return this._firstName?.toString();
  }

  public set firstName(value: string) {
    this._firstName = value;
  }

  public get lastName(): string {
    return this._lastName?.toString();
  }

  public set lastName(value: string) {
    this._lastName = value;
  }

  public get userRoles(): Role[] {
    return this._userRoles;
  }

  public set userRoles(value: Role[]) {
    this._userRoles = value;
  }

  public get password(): string {
    return this._password?.toString();
  }

  public set password(value: string) {
    this._password = value;
  }
  
  public get login(): string {
    return this._login?.toString();
  }

  public set login(value: string) {
    this._login = value;
  }

  public get tokens(): OAuth2AccessToken | undefined {
    return this._tokens;
  }

  public get avatar(): string {
    return this._avatar?.toString();
  }
  
  public set avatar(value: string) {
    this._avatar = value;
  }

  public get lastlogin(): string {
    return this._lastlogin?.toString();
  }

  public set lastlogin(value: string) {
    this._lastlogin = value;
  }

  public get enabled(): Boolean {
    return this._enabled;
  }

  public set enabled(value: Boolean) {
    this._enabled = value;
  }

  public get banned(): Boolean {
    return this._banned;
  }

  public set banned(value: Boolean) {
    this._banned = value;
  }

  public get verified(): Boolean {
    return this._verified;
  }
  public set verified(value: Boolean) {
    this._verified = value;
  }

  public get banReason(): string {
    return this._banReason?.toString();
  }

  public set banReason(value: string) {
    this._banReason = value;
  }

  static fromJsonString(json: string) {
    return new User(JSON.parse(json)['_tokens']);
  }

  public toJsonString(): string {
    return JSON.stringify(this);
  }

  public isEmpty(): boolean {
    throw new Error('Method not implemented.');
  }
}
