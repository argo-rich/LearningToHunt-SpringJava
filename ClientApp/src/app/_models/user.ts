export class User {
    userId?: number;
    email?: string;
    password?: string;
    confirmPassword?: string;
    firstName?: string;
    lastName?: string;
    emailConfirmed?: boolean;
    roles?: string[]
    token?: string;
}
