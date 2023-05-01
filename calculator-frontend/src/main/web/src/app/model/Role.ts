import { JsonObject, JsonProperty } from 'json2typescript';

/**
 * Class that strores info about user role
 * @author Alexander Zhulinsky
 * @version 1.0 26 Apr 2023
 */
@JsonObject('Role')
export class Role {
    @JsonProperty('role', String)
    private _name: String;
    @JsonProperty('description', String)
    private _description: String;
    @JsonProperty('privileges', [String])
    private _operations: String[];

    constructor(rolename?:String, description?: String, operation?:String[]) {
        this._name = rolename || '';
        this._description = description || '';
        this._operations = operation || [''];
    }

	public get operations(): String[] {
		return this._operations;
	}

	public set operations(value: String[]) {
		this._operations = value;
	}

	public get name(): String {
		return this._name;
	}

	public set name(value: String) {
		this._name = value;
	}

	public get description(): String {
		return this._description;
	}

	public set description(value: String) {
		this._description = value;
	}


}