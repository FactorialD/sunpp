package ua.factoriald.sunpp.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ua.factoriald.sunpp.model.ApplicationCheckingEntity;
import ua.factoriald.sunpp.model.ApplicationEntity;
import ua.factoriald.sunpp.model.ServiceEntity;
import ua.factoriald.sunpp.model.UserEntity;
import ua.factoriald.sunpp.model.constants.CheckTypeConstants;
import ua.factoriald.sunpp.model.constants.RoleConstants;
import ua.factoriald.sunpp.repository.*;
import ua.factoriald.sunpp.services.DataProcessController;

import java.util.Date;
import java.util.List;

/**
 * REST контроллер для роботи з даними, що стосуються роботи власника
 *
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ServiceOwnerController {

    private final DataProcessController dataProcessController;
    private final ServiceRepository serviceRepository;
    private final ApplicationRepository applicationRepository;
    private final RoleRepository roleRepository;
    private final ApplicationCheckingRepository checkingRepository;
    private final CheckTypeRepository checkTypeRepository;

    @Autowired
    public ServiceOwnerController(DataProcessController dataProcessController, ServiceRepository serviceRepository, ApplicationRepository applicationRepository, RoleRepository roleRepository, ApplicationCheckingRepository checkingRepository, CheckTypeRepository checkTypeRepository) {
        this.dataProcessController = dataProcessController;
        this.serviceRepository = serviceRepository;
        this.applicationRepository = applicationRepository;
        this.roleRepository = roleRepository;
        this.checkingRepository = checkingRepository;
        this.checkTypeRepository = checkTypeRepository;
    }

    /**
     * Повертає всі заявки від всіх сервісів власника
     * @param ownerId ідентифікатор власника
     * @return Список заявок або @null
     */
    @GetMapping("/owner/{owner_id}/application/all/service/all")
    public List<ApplicationEntity> getAllOwnerApplications(@PathVariable("owner_id") Long ownerId) {
        try{
            UserEntity owner = dataProcessController.getUserWithRoleOrThrow(
                    ownerId,
                    roleRepository.findById(RoleConstants.OWNER).get());

            List<ApplicationEntity> applications = applicationRepository.getAllByServiceIn(
                    serviceRepository.getAllByOwnerUser(owner));
            return applications;

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Повертає всі заявки від одного сервісу власника. Якщо це не сервіс власника, то помилка доступу
     * @param ownerId Ідентифікатор власника
     * @param serviceId Ідентифікатор сервісу
     * @return Список заявок або null
     */
    @GetMapping("/owner/{owner_id}/application/all/service/{service_id}")
    public List<ApplicationEntity> getAllOwnerApplicationsByService(@PathVariable("owner_id") Long ownerId,
                                                                    @PathVariable("service_id") Long serviceId) {
        try{
            UserEntity owner = dataProcessController.getUserWithRoleOrThrow(
                    ownerId,
                    roleRepository.findById(RoleConstants.OWNER).get());
            ServiceEntity service = dataProcessController.getServiceOrThrow(serviceId);
            dataProcessController.throwIfServiceNotOfOwner(service,owner);

            List<ApplicationEntity> applications = applicationRepository.getAllByService(service);
            return applications;

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Повертає всі заявки від всіх сервісів власника, що потребують уваги власника.
     * @param ownerId Ідентифікатор власника
     * @return Список заявок або null
     */
    @GetMapping("/owner/{owner_id}/application/refreshed/service/all")
    public List<ApplicationEntity> getAllOwnerRefreshedApplications(@PathVariable("owner_id") Long ownerId) {
        try{
            UserEntity ownerUser = dataProcessController.getUserWithRoleOrThrow(
                    ownerId,
                    roleRepository.findById(RoleConstants.OWNER).get());

            List<ApplicationEntity> allOwnerApplications = applicationRepository.getAllByServiceIn(
                    serviceRepository.getAllByOwnerUser(ownerUser)
            );//всі заявки, що відносяться до сервісу власника

            List<ApplicationEntity> refreshedApplications =
                    dataProcessController.getRefreshedApplicationsForOwner(allOwnerApplications);
            return refreshedApplications;

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Повертає всі заявки від одного сервісу власника, що потребують уваги власника. Якщо це не сервіс власника, то помилка доступу
     * @param serviceId Ідентифікатор сервісу
     * @param ownerId Ідентифікатор власника
     * @return Список заявок або null
     */
    @GetMapping("/owner/{owner_id}/application/refreshed/service/{service_id}")
    public List<ApplicationEntity> getAllOwnerRefreshedApplicationsByService(@PathVariable("owner_id") Long ownerId,
                                                                             @PathVariable("service_id") Long serviceId) {
        try {
            UserEntity ownerUser = dataProcessController.getUserWithRoleOrThrow(
                    ownerId,
                    roleRepository.findById(RoleConstants.OWNER).get());
            ServiceEntity service = dataProcessController.getServiceOrThrow(serviceId);
            dataProcessController.throwIfServiceNotOfOwner(service,ownerUser);

            List<ApplicationEntity> allOwnerApplications = applicationRepository.getAllByService(service);
            List<ApplicationEntity> refreshedApplications =
                    dataProcessController.getRefreshedApplicationsForOwner(allOwnerApplications);
            return refreshedApplications;

        }  catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Повертає одну заявку від сервісів власника. Якщо такої заявки нема, чи це не сервіс власника, то помилка доступу
     * @param applicationId Ідентифікатор заявки
     * @param ownerId Ідентифікатор власника
     * @return Заявка або null
     */
    @GetMapping("/owner/{owner_id}/application/{id}")
    public ApplicationEntity getOwnerApplication(@PathVariable("owner_id") Long ownerId,
                                                 @PathVariable("id") Long applicationId) {
        try{
            UserEntity ownerUser = dataProcessController.getUserWithRoleOrThrow(
                    ownerId,
                    roleRepository.findById(RoleConstants.OWNER).get());
            ApplicationEntity application = dataProcessController.getApplicationOrThrow(applicationId);
            dataProcessController.throwIfServiceNotOfOwner(application.getService(),ownerUser);

            return application;

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Приймає одну заявку. Якщо такої заявки нема, чи це не сервіс власника, то помилка доступу
     * @param applicationId Ідентифікатор заявки
     * @param ownerId Ідентифікатор власника
     * @param note Коментар (опціонально)
     */
    @GetMapping("/owner/{owner_id}/application/{id}/accept")
    public void acceptApplicationByOwner(@PathVariable("id") Long applicationId,
                                           @PathVariable("owner_id") Long ownerId,
                                           @RequestParam(value = "note", required = false) String note) {
        try{
            UserEntity ownerUser = dataProcessController.getUserWithRoleOrThrow(
                    ownerId,
                    roleRepository.findById(RoleConstants.OWNER).get());
            ApplicationEntity application = dataProcessController.getApplicationOrThrow(applicationId);
            dataProcessController.throwIfServiceNotOfOwner(application.getService(),ownerUser);

            ApplicationCheckingEntity checkRecord = null;
            //Дивимося записи перевірок, щоб зрозуміти, чи заявка очікує підтвердження від власника
            for (ApplicationCheckingEntity check: application.getCheckings() ) {//перевіряємо запис власника
                if(check.getCheckType().equals(checkTypeRepository.findById(CheckTypeConstants.CHECKING_RECORD).get()) &&
                        check.getRole().equals(roleRepository.findById(RoleConstants.OWNER).get()) ){//якщо це запис власника
                    if(check.getCheckYesNoNull() == null){
                        //все в порядку, ідемо далі
                        checkRecord = check;
                    }else if(check.getCheckYesNoNull()){//і він підтверджений
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Власник вже перевірив заявку");
                    }else{
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Власник вже відхилив заявку");
                    }
                }
            }

            //Приймаємо заявку
            //Власник, що прийняв заявку
            checkRecord.setUser(ownerUser);
            //Запис факту підтвердження заявки
            checkRecord.setCheckYesNoNull(true);
            //Дата прийняття заявки
            checkRecord.setCheckingDate(new java.sql.Timestamp(new Date().getTime()));
            //Опціональний коментар
            if(note != null){
                checkRecord.setNote(note);
            }
            //Зберігаємо запис перевірки
            checkingRepository.saveAndFlush(checkRecord);

            return;

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Не приймає одну заявку. Якщо такої заявки нема, чи це не сервіс власника, то помилка доступу
     * @param applicationId Ідентифікатор заявки
     * @param ownerId Ідентифікатор власника
     * @param note Коментар (опціонально)
     */
    @GetMapping("/owner/{owner_id}/application/{id}/decline")
    public void declineApplicationByOwner(@PathVariable("id") Long applicationId,
                                            @PathVariable("owner_id") Long ownerId,
                                            @RequestParam(value = "note", required = false) String note) {
        try{
            UserEntity ownerUser = dataProcessController.getUserWithRoleOrThrow(
                    ownerId,
                    roleRepository.findById(RoleConstants.OWNER).get());
            ApplicationEntity application = dataProcessController.getApplicationOrThrow(applicationId);
            dataProcessController.throwIfServiceNotOfOwner(application.getService(),ownerUser);

            ApplicationCheckingEntity checkRecord = null;
            //Дивимося записи перевірок, щоб зрозуміти, чи заявка очікує підтвердження від власника
            for (ApplicationCheckingEntity check: application.getCheckings() ) {//перевіряємо запис власника
                if(check.getCheckType().equals(checkTypeRepository.findById(CheckTypeConstants.CHECKING_RECORD).get()) &&
                        check.getRole().equals(roleRepository.findById(RoleConstants.OWNER).get()) ){//якщо це запис власника
                    if(check.getCheckYesNoNull() == null){
                        //все в порядку, ідемо далі
                        checkRecord = check;
                    }else if(check.getCheckYesNoNull()){//і він підтверджений
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Власник вже перевірив заявку");
                    }else{
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Власник вже відхилив заявку");
                    }
                }
            }

            //Відхиляємо заявку
            //Власник, що відхилив заявку
            checkRecord.setUser(ownerUser);
            //Запис факту підтвердження заявки
            checkRecord.setCheckYesNoNull(false);
            //Дата прийняття заявки
            checkRecord.setCheckingDate(new java.sql.Timestamp(new Date().getTime()));
            //Опціональний коментар
            if(note != null){
                checkRecord.setNote(note);
            }
            //Зберігаємо запис перевірки
            checkingRepository.saveAndFlush(checkRecord);

            return;

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
