package de.icod.techidon.model;

public class PaginatedResponse<T>{
	public T items;
	public String maxID;

	public PaginatedResponse(T items, String maxID){
		this.items=items;
		this.maxID=maxID;
	}
}
