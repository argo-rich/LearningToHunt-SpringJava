import {Role} from '@app/_models/Role';

export class User {
    userId?: number;
    email?: string;
    password?: string;
    confirmPassword?: string;
    firstName?: string;
    lastName?: string;
    emailConfirmed?: boolean;
    roles?: Role[];
    roleNames?: string[];
    token?: string;
}
