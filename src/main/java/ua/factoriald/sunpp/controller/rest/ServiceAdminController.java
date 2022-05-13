package ua.factoriald.sunpp.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ua.factoriald.sunpp.model.*;
import ua.factoriald.sunpp.model.constants.RoleConstants;
import ua.factoriald.sunpp.repository.*;
import ua.factoriald.sunpp.services.DataProcessService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * REST контроллер для роботи з даними, що стосуються роботи адміна
 *
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ServiceAdminController {

    private final DataProcessService dataService;
    private final WorkerRepository workerRepository;
    private final ApplicationCheckingRepository applicationCheckingRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserHaveAccessToServiceRepository accessToServiceRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    @Autowired
    public ServiceAdminController(DataProcessService dataService, WorkerRepository workerRepository, ApplicationCheckingRepository applicationCheckingRepository, RoleRepository roleRepository, UserRepository userRepository, UserHaveAccessToServiceRepository accessToServiceRepository, DepartmentRepository departmentRepository, PositionRepository positionRepository) {
        this.dataService = dataService;
        this.workerRepository = workerRepository;
        this.applicationCheckingRepository = applicationCheckingRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.accessToServiceRepository = accessToServiceRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
    }

    /**
     * Повертає всі заявки від усіх сервісів адміністратора
     * @param adminIdString Ідентифікатор адміністратора
     * @return Список заявок або null
     */
    @GetMapping("/admin/{admin_id}/application/all/service/all")
    public List<ApplicationEntity> getAllApplications(@PathVariable("admin_id") String adminIdString) {
        try{
            Long adminId = dataService.getLongOrThrow(adminIdString);

            UserEntity admin = dataService.getUserWithRoleOrThrow(
                    adminId,
                    roleRepository.findById(RoleConstants.ADMIN).get());

            return dataService.getAllApplicationsForAdmin(admin);

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Повертає одну заявку від сервісів адміністратора
     * @param adminIdString Ідентифікатор адміністратора
     * @param applicationIdString Ідентифікатор заявки
     * @return Заявка або null
     */
    @GetMapping("/admin/{admin_id}/application/{id}")
    public ApplicationEntity getApplication(@PathVariable("admin_id") String adminIdString,
                                            @PathVariable("id") String applicationIdString) {
        try{
            Long adminId = dataService.getLongOrThrow(adminIdString);
            Long applicationId = dataService.getLongOrThrow(applicationIdString);

            UserEntity adminUser = dataService.getUserWithRoleOrThrow(
                    adminId,
                    roleRepository.findById(RoleConstants.ADMIN).get());
            ApplicationEntity application = dataService.getApplicationOrThrow(applicationId);
            dataService.throwIfServiceNotOfAdmin(application.getService(),adminUser);

            return application;

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Повертає всі заявки, які очікують на рішення адміна
     * @param adminIdString Ідентифікатор адміністратора
     * @return Список заявок або null
     */
    @GetMapping("/admin/{admin_id}/application/refreshed")
    public List<ApplicationEntity> getAdminRefreshedApplications(@PathVariable("admin_id") String adminIdString) {
        try{
            Long adminId = dataService.getLongOrThrow(adminIdString);

            UserEntity admin = dataService.getUserWithRoleOrThrow(
                    adminId,
                    roleRepository.findById(RoleConstants.ADMIN).get());

            List<ApplicationEntity> allApplications = dataService.getAllApplicationsForAdmin(admin);
            return dataService.getRefreshedApplicationsForAdmin(allApplications);

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Приймає одну заявку
     * @param applicationIdString Ідентифікатор заявки
     * @param adminIdString Ідентифікатор адміністратора, що її приймає
     * @param note Коментар адміністратора
     */
    @GetMapping("/admin/{admin_id}/application/{id}/accept")
    public void acceptApplicationByAdmin(@PathVariable("id") String applicationIdString,
                                           @PathVariable("admin_id") String adminIdString,
                                           @RequestParam(value = "note", required = false) String note) {
        try {
            Long applicationId = dataService.getLongOrThrow(applicationIdString);
            Long adminId = dataService.getLongOrThrow(adminIdString);

            UserEntity adminUser = dataService.getUserWithRoleOrThrow(
                    adminId,
                    roleRepository.findById(RoleConstants.ADMIN).get());
            ApplicationEntity application = dataService.getApplicationOrThrow(applicationId);
            dataService.throwIfServiceNotOfAdmin(application.getService(), adminUser);

            HashMap<String, ApplicationCheckingEntity> checkings =
                    dataService.checkApplicationReadyForAdminAndGetCheckingsOrThrow(application);

            ApplicationCheckingEntity adminCheckRecord = checkings.get("adminCheckRecord");
            ApplicationCheckingEntity userCheckRecord = checkings.get("userCheckRecord");

            //Приймаємо заявку
            //Адмін, що прийняв заявку
            adminCheckRecord.setUser(adminUser);
            //Запис факту підтвердження заявки
            adminCheckRecord.setCheckYesNoNull(true);
            //Дата прийняття заявки
            adminCheckRecord.setCheckingDate(new java.sql.Timestamp(new Date().getTime()));
            //Опціональний коментар
            if(note != null){
                adminCheckRecord.setNote(note);
            }
            //Зберігаємо запис перевірки
            applicationCheckingRepository.saveAndFlush(adminCheckRecord);

            //Створюємо запис доступу
            UserHaveAccessToServiceEntity accessRecord = new UserHaveAccessToServiceEntity();
            //Вписуємо користувача
            accessRecord.setUser(application.getApplicant());
            //Вписуємо сервіс
            accessRecord.setService(application.getService());
            //Вписуємо роль
            accessRecord.setRole(userCheckRecord.getRole());
            //Вписуємо підрозділ (опціонально)
            if(application.getDepartment() != null){
                accessRecord.setDepartment(application.getDepartment());
            }
            //Зберігаємо запис доступу
            accessToServiceRepository.saveAndFlush(accessRecord);

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Відхилює одну заявку
     * @param applicationIdString Ідентифікатор заявки
     * @param adminIdString Ідентифікатор адміна
     * @param note Коментар адміністратора
     */
    @GetMapping("/admin/{admin_id}/application/{id}/decline")
    public void declineApplicationByAdmin(@PathVariable("id") String applicationIdString,
                                            @PathVariable("admin_id") String adminIdString,
                                            @RequestParam(value = "note", required = false) String note) {
        try {
            Long applicationId = dataService.getLongOrThrow(applicationIdString);
            Long adminId = dataService.getLongOrThrow(adminIdString);

            UserEntity adminUser = dataService.getUserWithRoleOrThrow(
                    adminId,
                    roleRepository.findById(RoleConstants.ADMIN).get());
            ApplicationEntity application = dataService.getApplicationOrThrow(applicationId);
            dataService.throwIfServiceNotOfAdmin(application.getService(), adminUser);

            HashMap<String, ApplicationCheckingEntity> checkings =
                    dataService.checkApplicationReadyForAdminAndGetCheckingsOrThrow(application);

            ApplicationCheckingEntity adminCheckRecord = checkings.get("adminCheckRecord");

            //Відхилюємо заявку
            //Адмін, що прийняв заявку
            adminCheckRecord.setUser(adminUser);
            //Запис факту відхилення заявки
            adminCheckRecord.setCheckYesNoNull(false);
            //Дата відхилення заявки
            adminCheckRecord.setCheckingDate(new java.sql.Timestamp(new Date().getTime()));
            //Опціональний коментар
            if(note != null){
                adminCheckRecord.setNote(note);
            }
            //Зберігаємо запис перевірки
            applicationCheckingRepository.saveAndFlush(adminCheckRecord);

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Повертає всіх користувачів
     * @return Список користувачів
     */
    @GetMapping("/admin/user/all")
    public List<UserEntity> getAllUsers(){

        return userRepository.findAll();
    }

    /**
     * Повертає одного користувача
     * @param userIdString Ідентифікатор користувача
     * @return Користувач або null
     */
    @GetMapping("/admin/user/{id}")
    public UserEntity getUser(@PathVariable("id") String userIdString) {
        try {
            Long userId = dataService.getLongOrThrow(userIdString);

            return dataService.getUserOrThrow(userId);

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Повертає робітників
     * @return Список робітників
     */
    @GetMapping("/admin/worker/all")
    public List<WorkerEntity> getAllWorkers(){

        return workerRepository.findAll();
    }

    /**
     * Повертає одного робітника
     * @param workerIdString Ідентифікатор робітника
     * @return Робітник або null
     */
    @GetMapping("/admin/worker/{id}")
    public WorkerEntity getWorker(@PathVariable("id") String workerIdString) {
        try {
            Long workerId = dataService.getLongOrThrow(workerIdString);

            return dataService.getWorkerOrThrow(workerId);

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Повертає всі підрозділи
     * @return Список підрозділів
     */
    @GetMapping("/admin/department/all")
    public List<DepartmentEntity> getAllDepartments(){

        return departmentRepository.findAll();
    }

    /**
     * Повертає всі посади
     * @return Список посад
     */
    @GetMapping("/admin/position/all")
    public List<PositionEntity> getAllPositions(){

        return positionRepository.findAll();
    }

}
