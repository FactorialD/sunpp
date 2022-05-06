package ua.factoriald.sunpp.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.factoriald.sunpp.model.*;
import ua.factoriald.sunpp.model.constants.CheckTypeConstants;
import ua.factoriald.sunpp.model.constants.RoleConstants;
import ua.factoriald.sunpp.repository.ApplicationRepository;
import ua.factoriald.sunpp.repository.CheckTypeRepository;
import ua.factoriald.sunpp.repository.RoleRepository;
import ua.factoriald.sunpp.repository.ServiceRepository;
import ua.factoriald.sunpp.services.DataProcessController;
import ua.factoriald.sunpp.services.DataProcessException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Клас працює з адресами, що може відкрити користувач
 * Контролер повертає відповіді в стилі REST
 *
 */
@RestController
public class ServiceUserController {

    private final DataProcessController dataProcessController;
    private final ApplicationRepository applicationRepository;
    private final ServiceRepository serviceRepository;
    private final CheckTypeRepository checkTypeRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public ServiceUserController(DataProcessController dataProcessController, ApplicationRepository applicationRepository, ServiceRepository serviceRepository, CheckTypeRepository checkTypeRepository, RoleRepository roleRepository) {
        this.dataProcessController = dataProcessController;
        this.applicationRepository = applicationRepository;
        this.serviceRepository = serviceRepository;
        this.checkTypeRepository = checkTypeRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Створює одну заявку
     * @param userId Ідентифікатор користувача-заявляча
     * @param serviceId Ідентифікатор потрібного сервісу
     * @param roleId Ідентифікатор потрібної ролі
     * @param departmentId Ідентифікатор підрозділу (опціонально)
     * @param note Коментар (опціонально)
     * @param response через цей об'єкт виконується редірект
     * @throws IOException якщо будуть проблеми з редіректом
     */
    @GetMapping("/user/{user_id}/application/create/{service_id}/{role_id}/")
    public void createApplication(@PathVariable("user_id") Long userId,
                                  @PathVariable("service_id") Long serviceId,
                                  @PathVariable("role_id") Long roleId,
                                  @RequestParam(value = "department_id", required = false) Long departmentId,
                                  @RequestParam(value = "note", required = false) String note,
                                  HttpServletResponse response) throws IOException {
        try {
            UserEntity user = dataProcessController.getUserOrThrow(userId);
            ServiceEntity service = dataProcessController.getServiceOrThrow(serviceId);
            RoleEntity role = dataProcessController.getRoleOrThrow(roleId);
            DepartmentEntity department = null;
            if(departmentId != null){
                department = dataProcessController.getDepartmentOrThrow(departmentId);
            }
            if(!service.getAvaliableRoles().contains(role)){
                throw new DataProcessException("Сервіс не має такої ролі");
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

            //зберігаємо заявку
            applicationRepository.saveAndFlush(application);

            response.sendRedirect("/service/all");
            return;

        } catch (DataProcessException e) {
            e.printStackTrace();
            response.sendRedirect("/error");
            return;
        }
    }

    /**
     * Повертає всі сервіси
     * @return Список сервісів
     */
    @GetMapping("/service/all")
    public List<ServiceEntity> getAllServices(){

        List<ServiceEntity> services = serviceRepository.findAll();
        System.out.println(services.toString());
        return services;
    }

    /**
     * Повертає один сервіс і інформацію про нього
     * @param id Ідентифікатор сервісу
     * @param response через цей об'єкт виконується редірект
     * @return Сервіс або null
     * @throws IOException якщо будуть проблеми з редіректом
     */
    @GetMapping("/service/{id}")
    public ServiceEntity getService(@PathVariable("id") Long id,
                                    HttpServletResponse response) throws IOException {
        try {
            ServiceEntity service = dataProcessController.getServiceOrThrow(id);
            return service;

        } catch (DataProcessException e) {
            e.printStackTrace();
            response.sendRedirect("/error");
            return null;
        }
    }

}
