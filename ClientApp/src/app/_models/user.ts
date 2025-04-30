export class User {
    userId?: string;
    email?: string;
    password?: string;
    confirmPassword?: string;
    firstName?: string;
    lastName?: string;
    emailConfirmed?: boolean;
    roles?: string[]
    token?: string;
}
