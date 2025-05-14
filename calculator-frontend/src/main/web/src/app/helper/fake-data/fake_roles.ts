import { Role } from '../../model/Role';

export const adminRole = new Role(1, 'ROLE_ADMIN', 'admin', ['READ_PRIVILEGE', 'WRITE_PRIVILEGE', 'WRITE_PRIVILEGE', 'DELETE_PRIVILEGE']);
export const mentorRole = new Role(2, 'ROLE_MENTOR', 'mentor', []);
export const userRole = new Role(3, 'ROLE_USER', 'user', []);
export const moderatorRole = new Role(4, 'ROLE_MODERATOR', 'moderator', []);

export const ALL_ROLES = [  adminRole,
                            mentorRole,
                            userRole,
                            moderatorRole];

