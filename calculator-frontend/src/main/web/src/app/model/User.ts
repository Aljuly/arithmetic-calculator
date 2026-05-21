import { JwtHelperService } from '@auth0/angular-jwt';
import { Entity } from './Entity';
import { OAuth2AccessToken } from './OAuth2AccessToken';
import { Role } from './Role';

/**
 * Class that strores info about registered user
 * @author Alexander Zhulinsky
 * @version 2.0 26 Apr 2023
 */
export class User extends Entity {

  private _firstName: String;
  private _lastName: String;
  private _login: String;
  private _avatar: String;
  private _email: String;
  private _password: String;
  private _userRoles: Role[] = new Array();
  private _lastlogin: String;
  private _enabled: Boolean;
  private _banned: Boolean;
  private _verified: Boolean;
  private _banReason: String;

  constructor(private _tokens?: OAuth2AccessToken) {
    super(0);
    if (_tokens && _tokens.access_token) {
      const token = new JwtHelperService().decodeToken(
        _tokens.access_token.toString()
      );
      const accessToken = JSON.parse(token['sub']);
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
        roles.forEach((el: { id: number, name: string; description: string }) => {
          const role = new Role(el.id, el.name, el.description);
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

  static fromJson(userDto: any): User {
    if (typeof userDto === 'string' || userDto instanceof String) {
      userDto = JSON.parse(userDto.toString());
    }
    const user: User = new User();
    user.id = userDto.id;
    user.firstName = userDto.firstName || '';
    user.lastName = userDto.lastName || '';
    user.avatar = userDto.avatar || '';
    user.login = userDto.login || '';
    user.email = userDto.email || '';
    user.password = userDto.password || '';
    user.lastlogin = userDto.lastlogin || '';
    user.enabled = userDto.enabled || false;
    user.banned = userDto.banned || false;
    user.verified = userDto.verified || false;
    user.banReason = userDto.banReason || '';
    const roles = userDto.userRoles;
    if (roles) {
        roles.forEach(role => {
        user.userRoles.push(new Role(role.id, role.name, role.description, role.operations));
      });
    }
    user.tokens = userDto.tokens ? userDto.tokens : null;
    return user;
  }

  static fromAccessToken(json: string): User {
    const user = JSON.parse(json);
    return new User(user._tokens);
  }

  public toJson(): string {
    return JSON.stringify({
      id: this.id,
      firstName: this._firstName,
      lastName: this._lastName,
      login: this._login,
      email: this._email,
      password: this._password,
      avatar: this._avatar,
      lastlogin: this._lastlogin,
      enabled: this._enabled,
      banned: this._banned,
      verified: this._verified,
      banReason: this._banReason,
      userRoles: this._userRoles.map(role => role.toJson()),
      tokens: this._tokens ? this._tokens : null
    });
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

  public set tokens(value: OAuth2AccessToken | undefined) {
    this._tokens = value;
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

  public isEmpty(): boolean {
    throw new Error('Method not implemented.');
  }
}
