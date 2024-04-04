import { JsonObject, JsonProperty } from 'json2typescript';

@JsonObject('Entity')
export abstract class Entity {
    @JsonProperty('id', Number)
    private _id: number;
    // the constructor
    constructor() {
        this._id = 0;
    }
    // getter and setter
    public get id(): number {
        return this._id;
    }
    public set id(value: number) {
        this._id = value;
    }
    // defines whereas object is empty
    public abstract isEmpty(): boolean;
}