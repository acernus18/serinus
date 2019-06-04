package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.maples.serinus.model.RoleResources;
import org.maples.serinus.model.SerinusProduct;
import org.maples.serinus.model.SerinusResources;
import org.maples.serinus.model.SerinusRole;
import org.maples.serinus.repository.RoleResourcesMapper;
import org.maples.serinus.repository.SerinusProductMapper;
import org.maples.serinus.repository.SerinusResourcesMapper;
import org.maples.serinus.repository.SerinusRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private SerinusProductMapper productMapper;

    @Autowired
    private SerinusRoleMapper roleMapper;

    @Autowired
    private SerinusResourcesMapper resourcesMapper;

    @Autowired
    private RoleResourcesMapper roleResourcesMapper;

    @Autowired
    private PermissionService permissionService;

    @Transactional
    public void addProduct(String belongTo, String productName) {
        SerinusProduct product = new SerinusProduct();
        product.setBelongTo(belongTo);
        product.setProductName(productName);
        product.setStatus((byte) 0);
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());

        productMapper.insert(product);

        permissionService.addSerinusRole(product.getProductName() + "_editor");
        permissionService.addSerinusRole(product.getProductName() + "_inspector");

        permissionService.addSerinusResources("/strategy/edit", product.getProductName() + "_editor");
        permissionService.addSerinusResources("/strategy/inspect", product.getProductName() + "_inspector");

        permissionService.addRoleResources(product.getProductName() + "_editor", "/strategy/edit");
        permissionService.addRoleResources(product.getProductName() + "_inspector", "/strategy/inspect");
    }

    @Transactional
    public void deleteProduct(int productID) {
        SerinusProduct product = productMapper.selectByPrimaryKey(productID);

        if (product == null) {
            return;
        }

        SerinusProduct selective = new SerinusProduct();
        selective.setId(product.getId());
        selective.setStatus((byte) 1);

        productMapper.updateByPrimaryKeySelective(selective);

        permissionService.deleteSerinusRole(product.getProductName() + "_editor");
        permissionService.deleteSerinusRole(product.getProductName() + "_inspector");

        permissionService.deleteSerinusResources("/strategy/edit");
        permissionService.deleteSerinusResources("/strategy/inspect");

        permissionService.deleteRoleResources(product.getProductName() + "_editor", "/strategy/edit");
        permissionService.deleteRoleResources(product.getProductName() + "_inspector", "/strategy/inspect");
    }
}
