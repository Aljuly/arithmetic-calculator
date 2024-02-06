// import { JsonConvert } from 'json2typescript';
import { Role } from '../../model/Role';

export const adminRole = new Role('ROLE_ADMIN', 'admin', ['OP_USER_VIEW', 'OP_USER_EXTEND', 'OP_USER_MANAGE']);
export const mentorRole = new Role('ROLE_MENTOR', 'mentor', []);
export const userRole = new Role('ROLE_USER', 'user', []);
export const moderatorRole = new Role('ROLE_MODERATOR', 'moderator', []);

// const jsonConvert: JsonConvert = new JsonConvert();

export const ALL_ROLES = [  adminRole,
                            mentorRole,
                            userRole,
                            moderatorRole];

// export const ROLES = jsonConvert.serialize(ALL_ROLES, Role);