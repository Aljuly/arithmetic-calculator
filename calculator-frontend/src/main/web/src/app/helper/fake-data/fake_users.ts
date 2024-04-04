import { JsonConvert } from 'json2typescript';
import { User } from '../../model/User';
import { adminRole, userRole, mentorRole } from './fake_roles';

const jsonConvert: JsonConvert = new JsonConvert();

const admin: User = new User();
admin.id = 101;
admin.login = 'admin';
admin.password = '123456';
// admin.salt = 'salt';
admin.email = 'admin@admin';
admin.firstName = 'admin_first';
admin.lastName = 'admin_last';
admin.avatar = 'assets/fake/storage/adminId-1.png';
admin.lastlogin = new Date(2019, 1, 7).toDateString();
admin.enabled = true;
admin.banned = false;
admin.verified = true;
admin.banReason = '';
admin.userRoles = [adminRole, mentorRole];


const user: User = new User();
user.id = 102;
user.login = 'user';
user.password = '123456';
// user.salt = 'salt';
user.email = 'user@user';
user.firstName = 'user_first';
user.lastName = 'user_last';
user.avatar = 'assets/fake/storage/userId-1.png';
user.lastlogin = new Date(2019, 1, 7).toDateString();
user.enabled = true;
user.banned = false;
user.verified = true;
user.banReason = '';
user.userRoles = [ userRole ];

export const ALL_USERS: User[] = [
    admin,
    user
];

export const USERS = jsonConvert.serialize(ALL_USERS, User);