package ua.factoriald.sunpp.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ua.factoriald.sunpp.model.ApplicationCheckingEntity;
import ua.factoriald.sunpp.model.ApplicationEntity;
import ua.factoriald.sunpp.model.ServiceEntity;
import ua.factoriald.sunpp.model.UserEntity;
import ua.factoriald.sunpp.model.constants.RoleConstants;
import ua.factoriald.sunpp.repository.*;
import ua.factoriald.sunpp.services.DataProcessService;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * REST контроллер для роботи з даними, що стосуються роботи власника
 *
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ServiceOwnerController {

    private final DataProcessService dataService;
    private final ServiceRepository serviceRepository;
    private final ApplicationRepository applicationRepository;
    private final RoleRepository roleRepository;
    private final ApplicationCheckingRepository checkingRepository;

    @Autowired
    public ServiceOwnerController(DataProcessService dataService, ServiceRepository serviceRepository, ApplicationRepository applicationRepository, RoleRepository roleRepository, ApplicationCheckingRepository checkingRepository) {
        this.dataService = dataService;
        this.serviceRepository = serviceRepository;
        this.applicationRepository = applicationRepository;
        this.roleRepository = roleRepository;
        this.checkingRepository = checkingRepository;
    }

    /**
     * Повертає всі заявки від всіх сервісів власника
     * @param ownerIdString ідентифікатор власника
     * @return Список заявок або @null
     */
    @GetMapping("/owner/{owner_id}/application/all/service/all")
    public List<ApplicationEntity> getAllOwnerApplications(@PathVariable("owner_id") String ownerIdString) {
        try{
            Long ownerId = dataService.getLongOrThrow(ownerIdString);

            UserEntity owner = dataService.getUserWithRoleOrThrow(
                    ownerId,
                    roleRepository.findById(RoleConstants.OWNER).get());

            return applicationRepository.getAllByServiceIn(
                    serviceRepository.getAllByOwnerUser(owner));

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Повертає всі заявки від одного сервісу власника. Якщо це не сервіс власника, то помилка доступу
     * @param ownerIdString Ідентифікатор власника
     * @param serviceIdString Ідентифікатор сервісу
     * @return Список заявок або null
     */
    @GetMapping("/owner/{owner_id}/application/all/service/{service_id}")
    public List<ApplicationEntity> getAllOwnerApplicationsByService(@PathVariable("owner_id") String ownerIdString,
                                                                    @PathVariable("service_id") String serviceIdString) {
        try{
            Long ownerId = dataService.getLongOrThrow(ownerIdString);
            Long serviceId = dataService.getLongOrThrow(serviceIdString);

            UserEntity owner = dataService.getUserWithRoleOrThrow(
                    ownerId,
                    roleRepository.findById(RoleConstants.OWNER).get());
            ServiceEntity service = dataService.getServiceOrThrow(serviceId);
            dataService.throwIfServiceNotOfOwner(service,owner);

            return applicationRepository.getAllByService(service);

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Повертає всі заявки від всіх сервісів власника, що потребують уваги власника.
     * @param ownerIdString Ідентифікатор власника
     * @return Список заявок або null
     */
    @GetMapping("/owner/{owner_id}/application/refreshed/service/all")
    public List<ApplicationEntity> getAllOwnerRefreshedApplications(@PathVariable("owner_id") String ownerIdString) {
        try{
            Long ownerId = dataService.getLongOrThrow(ownerIdString);

            UserEntity ownerUser = dataService.getUserWithRoleOrThrow(
                    ownerId,
                    roleRepository.findById(RoleConstants.OWNER).get());

            List<ApplicationEntity> allOwnerApplications = applicationRepository.getAllByServiceIn(
                    serviceRepository.getAllByOwnerUser(ownerUser)
            );//всі заявки, що відносяться до сервісу власника

            return dataService.getRefreshedApplicationsForOwner(allOwnerApplications);

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Повертає всі заявки від одного сервісу власника, що потребують уваги власника. Якщо це не сервіс власника, то помилка доступу
     * @param serviceIdString Ідентифікатор сервісу
     * @param ownerIdString Ідентифікатор власника
     * @return Список заявок або null
     */
    @GetMapping("/owner/{owner_id}/application/refreshed/service/{service_id}")
    public List<ApplicationEntity> getAllOwnerRefreshedApplicationsByService(@PathVariable("owner_id") String ownerIdString,
                                                                             @PathVariable("service_id") String serviceIdString) {
        try {
            Long ownerId = dataService.getLongOrThrow(ownerIdString);
            Long serviceId = dataService.getLongOrThrow(serviceIdString);

            UserEntity ownerUser = dataService.getUserWithRoleOrThrow(
                    ownerId,
                    roleRepository.findById(RoleConstants.OWNER).get());
            ServiceEntity service = dataService.getServiceOrThrow(serviceId);
            dataService.throwIfServiceNotOfOwner(service,ownerUser);

            List<ApplicationEntity> allOwnerApplications = applicationRepository.getAllByService(service);

            return dataService.getRefreshedApplicationsForOwner(allOwnerApplications);

        }  catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Повертає одну заявку від сервісів власника. Якщо такої заявки нема, чи це не сервіс власника, то помилка доступу
     * @param applicationIdString Ідентифікатор заявки
     * @param ownerIdString Ідентифікатор власника
     * @return Заявка або null
     */
    @GetMapping("/owner/{owner_id}/application/{id}")
    public ApplicationEntity getOwnerApplication(@PathVariable("owner_id") String ownerIdString,
                                                 @PathVariable("id") String applicationIdString) {
        try{
            Long ownerId = dataService.getLongOrThrow(ownerIdString);
            Long applicationId = dataService.getLongOrThrow(applicationIdString);

            UserEntity ownerUser = dataService.getUserWithRoleOrThrow(
                    ownerId,
                    roleRepository.findById(RoleConstants.OWNER).get());
            ApplicationEntity application = dataService.getApplicationOrThrow(applicationId);
            dataService.throwIfServiceNotOfOwner(application.getService(),ownerUser);

            return application;

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Приймає одну заявку. Якщо такої заявки нема, чи це не сервіс власника, то помилка доступу
     * @param applicationIdString Ідентифікатор заявки
     * @param ownerIdString Ідентифікатор власника
     * @param note Коментар (опціонально)
     */
    @GetMapping("/owner/{owner_id}/application/{id}/accept")
    public void acceptApplicationByOwner(@PathVariable("id") String applicationIdString,
                                           @PathVariable("owner_id") String ownerIdString,
                                           @RequestParam(value = "note", required = false) String note) {
        try{
            Long ownerId = dataService.getLongOrThrow(ownerIdString);
            Long applicationId = dataService.getLongOrThrow(applicationIdString);

            UserEntity ownerUser = dataService.getUserWithRoleOrThrow(
                    ownerId,
                    roleRepository.findById(RoleConstants.OWNER).get());
            ApplicationEntity application = dataService.getApplicationOrThrow(applicationId);
            dataService.throwIfServiceNotOfOwner(application.getService(),ownerUser);

            ApplicationCheckingEntity ownerCheckRecord = dataService
                    .checkApplicationReadyForOwnerAndGetCheckingOrThrow(application);

            //Приймаємо заявку
            //Власник, що прийняв заявку
            ownerCheckRecord.setUser(ownerUser);
            //Запис факту підтвердження заявки
            ownerCheckRecord.setCheckYesNoNull(true);
            //Дата прийняття заявки
            ownerCheckRecord.setCheckingDate(new java.sql.Timestamp(new Date().getTime()));
            //Опціональний коментар
            if(note != null){
                ownerCheckRecord.setNote(note);
            }
            //Зберігаємо запис перевірки
            checkingRepository.saveAndFlush(ownerCheckRecord);

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Не приймає одну заявку. Якщо такої заявки нема, чи це не сервіс власника, то помилка доступу
     * @param applicationIdString Ідентифікатор заявки
     * @param ownerIdString Ідентифікатор власника
     * @param note Коментар (опціонально)
     */
    @GetMapping("/owner/{owner_id}/application/{id}/decline")
    public void declineApplicationByOwner(@PathVariable("id") String applicationIdString,
                                            @PathVariable("owner_id") String ownerIdString,
                                            @RequestParam(value = "note", required = false) String note) {
        try{
            Long ownerId = dataService.getLongOrThrow(ownerIdString);
            Long applicationId = dataService.getLongOrThrow(applicationIdString);

            UserEntity ownerUser = dataService.getUserWithRoleOrThrow(
                    ownerId,
                    roleRepository.findById(RoleConstants.OWNER).get());
            ApplicationEntity application = dataService.getApplicationOrThrow(applicationId);
            dataService.throwIfServiceNotOfOwner(application.getService(),ownerUser);

            ApplicationCheckingEntity ownerCheckRecord = dataService
                    .checkApplicationReadyForOwnerAndGetCheckingOrThrow(application);

            //Відхиляємо заявку
            //Власник, що відхилив заявку
            ownerCheckRecord.setUser(ownerUser);
            //Запис факту підтвердження заявки
            ownerCheckRecord.setCheckYesNoNull(false);
            //Дата прийняття заявки
            ownerCheckRecord.setCheckingDate(new Timestamp(new Date().getTime()));
            //Опціональний коментар
            if(note != null){
                ownerCheckRecord.setNote(note);
            }
            //Зберігаємо запис перевірки
            checkingRepository.saveAndFlush(ownerCheckRecord);

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
