package ua.factoriald.sunpp.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ua.factoriald.sunpp.model.*;
import ua.factoriald.sunpp.model.constants.CheckTypeConstants;
import ua.factoriald.sunpp.model.constants.RoleConstants;
import ua.factoriald.sunpp.repository.ApplicationRepository;
import ua.factoriald.sunpp.repository.CheckTypeRepository;
import ua.factoriald.sunpp.repository.RoleRepository;
import ua.factoriald.sunpp.repository.ServiceRepository;
import ua.factoriald.sunpp.services.DataProcessService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * REST контроллер для роботи з даними, що стосуються роботи користувача
 *
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ServiceUserController {

    private final DataProcessService dataService;
    private final ApplicationRepository applicationRepository;
    private final ServiceRepository serviceRepository;
    private final CheckTypeRepository checkTypeRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public ServiceUserController(DataProcessService dataService, ApplicationRepository applicationRepository, ServiceRepository serviceRepository, CheckTypeRepository checkTypeRepository, RoleRepository roleRepository) {
        this.dataService = dataService;
        this.applicationRepository = applicationRepository;
        this.serviceRepository = serviceRepository;
        this.checkTypeRepository = checkTypeRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Створює одну заявку
     * @param userIdString Ідентифікатор користувача-заявляча
     * @param serviceIdString Ідентифікатор потрібного сервісу
     * @param roleIdString Ідентифікатор потрібної ролі
     * @param departmentIdString Ідентифікатор підрозділу (опціонально)
     * @param note Коментар (опціонально)
     */
    @GetMapping("/user/{user_id}/application/create/{service_id}/{role_id}/")
    public ApplicationEntity createApplication(@PathVariable("user_id") String userIdString,
                                  @PathVariable("service_id") String serviceIdString,
                                  @PathVariable("role_id") String roleIdString,
                                  @RequestParam(value = "department_id", required = false) String departmentIdString,
                                  @RequestParam(value = "note", required = false) String note) {
        try {
            Long userId = dataService.getLongOrThrow(userIdString);
            Long serviceId = dataService.getLongOrThrow(serviceIdString);
            Long roleId = dataService.getLongOrThrow(roleIdString);
            Long departmentId = dataService.getLongOrThrow(departmentIdString);

            UserEntity user = dataService.getUserOrThrow(userId);
            ServiceEntity service = dataService.getServiceOrThrow(serviceId);
            RoleEntity role = dataService.getRoleOrThrow(roleId);
            DepartmentEntity department = dataService.getDepartmentOrThrow(departmentId);

            if(!service.getAvaliableRoles().contains(role)){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Сервіс не має такої ролі");
            }

            //Створюємо заявку
            ApplicationEntity application = new ApplicationEntity();
            //Вписуємо користувача
            application.setApplicant(user);
            //Вписуємо дату створення
            application.setCreationDate(new java.sql.Timestamp(new Date().getTime()));
            //Вписуємо сервіс
            application.setService(service);
            //Вписуємо підрозділ (опціонально)
            if(department != null){
                application.setDepartment(department);
            }

            //Створюємо запис перевірки користувача
            ApplicationCheckingEntity userChecking = new ApplicationCheckingEntity();
            //Вписуємо тип запису
            userChecking.setCheckType(checkTypeRepository.findById(CheckTypeConstants.USER_APPLICATION_RECORD).get());
            //Вписуємо заявку
            userChecking.setApplication(application);
            //Вписуємо заявника
            userChecking.setUser(user);
            //Вписуємо роль заявника
            userChecking.setRole(role);
            //Вписуємо факт того, що він бачив цю заявку
            userChecking.setCheckYesNoNull(true);
            //Вписуємо дату створення
            userChecking.setCheckingDate(application.getCreationDate());
            //Вписуємо коментар (опціонально)
            if(note != null){
                userChecking.setNote(note);
            }

            //Створюємо запис перевірки власника
            ApplicationCheckingEntity ownerChecking = new ApplicationCheckingEntity();
            //Вписуємо тип запису
            ownerChecking.setCheckType(checkTypeRepository.findById(CheckTypeConstants.CHECKING_RECORD).get());
            //Вписуємо заявку
            ownerChecking.setApplication(application);
            //Вписуємо роль власника
            ownerChecking.setRole(roleRepository.findById(RoleConstants.OWNER).get());
            //Вписуємо факт того, що власник не бачив цю заявку
            ownerChecking.setCheckYesNoNull(null);

            //Створюємо запис перевірки адміна
            ApplicationCheckingEntity adminChecking = new ApplicationCheckingEntity();
            //Вписуємо тип запису
            adminChecking.setCheckType(checkTypeRepository.findById(CheckTypeConstants.CHECKING_RECORD).get());
            //Вписуємо заявку
            adminChecking.setApplication(application);
            //Вписуємо роль власника
            adminChecking.setRole(roleRepository.findById(RoleConstants.ADMIN).get());
            //Вписуємо факт того, що aдмін не бачив цю заявку
            adminChecking.setCheckYesNoNull(null);

            //додаємо записи до заявки
            application.setCheckings(new ArrayList<>(Arrays.asList(
                    userChecking,ownerChecking,adminChecking)));

            //зберігаємо заявку і повертаємо
            return applicationRepository.saveAndFlush(application);

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Повертає всі сервіси
     * @return Список сервісів
     */
    @GetMapping("/service/all")
    public List<ServiceEntity> getAllServices(){

        return serviceRepository.findAll();
    }

    /**
     * Повертає один сервіс і інформацію про нього
     * @param serviceIdString Ідентифікатор сервісу
     * @return Сервіс або null
     */
    @GetMapping("/service/{service_id}")
    public ServiceEntity getService(@PathVariable("service_id") String serviceIdString) {
        try {
            Long serviceId = dataService.getLongOrThrow(serviceIdString);

            return dataService.getServiceOrThrow(serviceId);

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Повертає усі заявки одного користувача
     * @param userIdString Ідентифікатор заявки
     * @return Список заявок або null
     */
    @GetMapping("/user/{user_id}/application/all")
    public List<ApplicationEntity> getWorkerApplications(@PathVariable("user_id") String userIdString) {
        try {
            Long userId = dataService.getLongOrThrow(userIdString);

            UserEntity user = dataService.getUserOrThrow(userId);

            return applicationRepository.getAllByApplicant(user);

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
