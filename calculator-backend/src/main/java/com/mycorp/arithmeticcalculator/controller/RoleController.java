package com.mycorp.arithmeticcalculator.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mycorp.arithmeticcalculator.dto.RoleDto;
import com.mycorp.arithmeticcalculator.error.RoleNotFoundException;
import com.mycorp.arithmeticcalculator.error.RoleProcessException;
import com.mycorp.arithmeticcalculator.service.IRoleService;
import com.mycorp.arithmeticcalculator.service.RoleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Validated
@Api(value = "roles", description = "the role API")
@RestController
public class RoleController {
	
	private final IRoleService roleService;

	public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}
	
    @ApiOperation(value = "Retrieve Roles list", nickname = "getAllRoles", response = List.class, 
    		tags = {"role-endpoint"})
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response = Resource.class)
    })
    @GetMapping(value = "/api/roles", produces = { "application/json" })
	public ResponseEntity<List<RoleDto>> getAll() {
		return ResponseEntity.ok(roleService.getAllRoles());
	}
	
    @ApiOperation(value = "Delete Role", nickname = "deleteRole", response = String.class, 
    		tags = {"role-endpoint"})
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response = Resource.class),
    		@ApiResponse(code = 404, message = "Not Found")
    })
    @DeleteMapping(value = "/api/roles/{roleId}", produces = { "application/json" })
	public ResponseEntity<Resource> delete(@PathVariable("roleId") Long roleId) throws RoleNotFoundException {
    	roleService.deleteRole(roleId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@ApiOperation(value = "Create Role", nickname = "saveRole", response = String.class, 
			tags = {"role-endpoint"})
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "Created", response = String.class)
    })
	@PostMapping(value ="/api/roles", produces = {"application/json"}, consumes = {"application/json"})
	public ResponseEntity<RoleDto> create(@RequestBody RoleDto role) throws RoleProcessException {
		return ResponseEntity.ok(roleService.createRole(role));
	}
	
	// Not implemented
	@ApiOperation(value = "Save Role", nickname = "saveRole", response = String.class, 
			tags = {"role-endpoint"})
    @ApiResponses(value = { 
            @ApiResponse(code = 200, message = "Saved", response = String.class),
            @ApiResponse(code = 400, message = "Invalid status value", response = String.class),
    })
	@PutMapping(value ="/api/roles", produces = {"application/json"}, consumes = {"application/json"})
	public ResponseEntity<Resource> update(@RequestBody RoleDto role) {
		return ResponseEntity.notFound().build();
	}
}
