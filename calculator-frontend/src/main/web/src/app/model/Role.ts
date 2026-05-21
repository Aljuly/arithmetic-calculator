import { Entity } from './entity';

/**
 * Class that strores info about user role
 * @author Alexander Zhulinsky
 * @version 1.0 26 Apr 2023
 */
export class Role extends Entity {

    private _name: string;
    private _description: string;
    private _operations: string[];

    constructor(id?: number, name?: string, description?: string, operations?: string[]) {
        super(id);
        this._name = name || '';
        this._description = description || '';
        this._operations = operations || [''];
    }

    static fromRoleDto(roleDto: any): Role {
        const role: Role = new Role(roleDto.id);
        role._name = roleDto.name;
        role._description = roleDto.description;
        if (roleDto.operations) {
            roleDto.operations.forEach((o: any) => {
                if (!o.name) {
                    role._operations.push(o);
                } else if (o.checked) {
                    role._operations.push(o.name);
                }
            });
        }
        role._operations = role._operations.filter((o: string) => o !== '');
        return role;
    }

    static fromJson(json: string): Role {
        const role = JSON.parse(json);
        return new Role(role.id, role.name, role.description, role.operations);
    }

    public toJson(): object {
        return {
            id: this.id,
            name: this._name,
            description: this._description,
            operations: this._operations
        };
    }
    
	public get operations(): string[] {
		return this._operations;
	}

	public set operations(value: string[]) {
		this._operations = value;
	}

	public get name(): string {
		return this._name?.toString();
	}

	public set name(value: string) {
		this._name = value;
	}

	public get description(): string {
		return this._description;
	}

	public set description(value: string) {
		this._description = value;
	}

    public isEmpty(): boolean {
        throw new Error('Method not implemented.');
    } 

}