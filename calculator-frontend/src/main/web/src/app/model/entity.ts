export abstract class Entity {

    private _id: number;

    // the constructor
    constructor(_id?: number) {
        this._id = _id ?? 0;
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