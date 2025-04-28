export class User {
    id?: string;
    email?: string;
    password?: string;
    confirmPassword?: string;
    firstName?: string;
    lastName?: string;
    emailConfirmed?: boolean;
    roles?: string[]
    token?: string;
}