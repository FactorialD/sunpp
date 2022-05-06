package ua.factoriald.sunpp.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.factoriald.sunpp.model.*;
import ua.factoriald.sunpp.model.constants.CheckTypeConstants;
import ua.factoriald.sunpp.model.constants.RoleConstants;
import ua.factoriald.sunpp.repository.*;
import ua.factoriald.sunpp.services.DataProcessController;
import ua.factoriald.sunpp.services.DataProcessException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;


/**
 * Клас працює з адресами, що має використовувати тільки адмін
 * Контролер повертає відповіді в стилі REST
 *
 */
@RestController
public class ServiceAdminController {

    private final DataProcessController dataProcessController;
    private final WorkerRepository workerRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationCheckingRepository applicationCheckingRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserHaveAccessToServiceRepository accessToServiceRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final CheckTypeRepository checkTypeRepository;

    @Autowired
    public ServiceAdminController(DataProcessController dataProcessController, WorkerRepository workerRepository, ApplicationRepository applicationRepository, ApplicationCheckingRepository applicationCheckingRepository, RoleRepository roleRepository, UserRepository userRepository, UserHaveAccessToServiceRepository accessToServiceRepository, DepartmentRepository departmentRepository, PositionRepository positionRepository, CheckTypeRepository checkTypeRepository) {
        this.dataProcessController = dataProcessController;
        this.workerRepository = workerRepository;
        this.applicationRepository = applicationRepository;
        this.applicationCheckingRepository = applicationCheckingRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.accessToServiceRepository = accessToServiceRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.checkTypeRepository = checkTypeRepository;
    }

    /**
     * Повертає всі заявки від усіх сервісів адміністратора
     * @param adminId Ідентифікатор адміністратора
     * @param response
     * @return Список заявок або null
     * @throws IOException
     */
    @GetMapping("/admin/{admin_id}/application/all/service/all")
    public List<ApplicationEntity> getAllApplications(@PathVariable("admin_id") Long adminId,
                                                      HttpServletResponse response) throws IOException {
        try{
            UserEntity admin = dataProcessController.getUserWithRoleOrThrow(
                    adminId,
                    roleRepository.findById(RoleConstants.ADMIN).get());

            List<ApplicationEntity> applications = dataProcessController.getAllApplicationsForAdmin(admin);
            return applications;

        } catch (DataProcessException e) {
            e.printStackTrace();
            response.sendRedirect("/error");
            return null;
        }
    }

    /**
     * Повертає одну заявку від сервісів адміністратора
     * @param adminId Ідентифікатор адміністратора
     * @param applicationId Ідентифікатор заявки
     * @param response
     * @return Заявка або null
     * @throws IOException
     */
    @GetMapping("/admin/{admin_id}/application/{id}")
    public ApplicationEntity getApplication(@PathVariable("admin_id") Long adminId,
                                            @PathVariable("id") Long applicationId,
                                            HttpServletResponse response) throws IOException {
        try{
            UserEntity adminUser = dataProcessController.getUserWithRoleOrThrow(
                    adminId,
                    roleRepository.findById(RoleConstants.ADMIN).get());
            ApplicationEntity application = dataProcessController.getApplicationOrThrow(applicationId);
            dataProcessController.throwIfServiceNotOfAdmin(application.getService(),adminUser);

            return application;

        } catch (DataProcessException e) {
            e.printStackTrace();
            response.sendRedirect("/error");
            return null;
        }
    }

    /**
     * Повертає всі заявки, які очікують на рішення адміна
     * @param adminId Ідентифікатор адміністратора
     * @param response
     * @return Список заявок або null
     * @throws IOException
     */
    @GetMapping("/admin/{admin_id}/application/refreshed")
    public List<ApplicationEntity> getAdminRefreshedApplications(@PathVariable("admin_id") Long adminId,
                                                                 HttpServletResponse response) throws IOException{
        try{
            UserEntity admin = dataProcessController.getUserWithRoleOrThrow(
                    adminId,
                    roleRepository.findById(RoleConstants.ADMIN).get());

            List<ApplicationEntity> allApplications = dataProcessController.getAllApplicationsForAdmin(admin);
            List<ApplicationEntity> refreshedApplications =
                    dataProcessController.getRefreshedApplicationsForAdmin(allApplications);
            return refreshedApplications;

        } catch (DataProcessException e) {
            e.printStackTrace();
            response.sendRedirect("/error");
            return null;
        }
    }

    /**
     * Приймає одну заявку
     * @param applicationId Ідентифікатор заявки
     * @param adminId Ідентифікатор адміністратора, що її приймає
     * @param note Коментар адміністратора
     * @param response через цей об'єкт виконується редірект
     * @throws IOException
     */
    @GetMapping("/admin/{admin_id}/application/{id}/accept")
    public void acceptApplicationByAdmin(@PathVariable("id") Long applicationId,
                                           @PathVariable("admin_id") Long adminId,
                                           @RequestParam(value = "note", required = false) String note,
                                           HttpServletResponse response) throws IOException {
        try {
            UserEntity adminUser = dataProcessController.getUserWithRoleOrThrow(
                    adminId,
                    roleRepository.findById(RoleConstants.ADMIN).get());
            ApplicationEntity application = dataProcessController.getApplicationOrThrow(applicationId);
            dataProcessController.throwIfServiceNotOfAdmin(application.getService(), adminUser);

            ApplicationCheckingEntity checkRecord = null;
            ApplicationCheckingEntity userCheckRecord = null;
            //Дивимося записи перевірок, щоб зрозуміти, чи заявка очікує підтвердження від адміна
            for (ApplicationCheckingEntity check: application.getCheckings() ) {//перевіряємо записи власника і адміністратора
                if(check.getCheckType().equals(checkTypeRepository.findById(CheckTypeConstants.CHECKING_RECORD).get()) &&
                        check.getRole().equals(roleRepository.findById(RoleConstants.OWNER).get()) ){//якщо це запис власника
                    if(check.getCheckYesNoNull() == null){
                        throw new DataProcessException("Власник ще не перевірив заявку");
                    }else if(check.getCheckYesNoNull()){//і він підтверджений
                        //то все в порядку, ідемо далі
                    }else{
                        throw new DataProcessException("Власник відхилив заявку");
                    }
                } else if (check.getCheckType().equals(checkTypeRepository.findById(CheckTypeConstants.CHECKING_RECORD).get()) &&
                        check.getRole().equals(roleRepository.findById(RoleConstants.ADMIN).get()) ){//якщо це запис адміна
                    if(check.getCheckYesNoNull() == null){
                        //адмін ще не перевірив заявку
                        checkRecord = check;
                    }else if(check.getCheckYesNoNull()){//і він підтверджений
                        throw new DataProcessException("Адміністратор вже прийняв заявку");
                    }else{
                        throw new DataProcessException("Адміністратор вже відхилив заявку");
                    }
                }else{//це запис користувача, нічого не робимо
                    //збережемо на потім
                    userCheckRecord = check;
                }
            }

            //Приймаємо заявку
            //Адмін, що прийняв заявку
            checkRecord.setUser(adminUser);
            //Запис факту підтвердження заявки
            checkRecord.setCheckYesNoNull(true);
            //Дата прийняття заявки
            checkRecord.setCheckingDate(new java.sql.Timestamp(new Date().getTime()));
            //Опціональний коментар
            if(note != null){
                checkRecord.setNote(note);
            }
            //Зберігаємо запис перевірки
            applicationCheckingRepository.saveAndFlush(checkRecord);

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

            response.sendRedirect("/admin/"+ adminId + "/application/refreshed");
            return;

        }catch (DataProcessException e) {
            e.printStackTrace();
            response.sendRedirect("/error");
            return;
        }
    }

    /**
     * Відхилює одну заявку
     * @param applicationId Ідентифікатор заявки
     * @param adminId Ідентифікатор адміна
     * @param note Коментар адміністратора
     * @param response через цей об'єкт виконується редірект
     * @throws IOException
     */
    @GetMapping("/admin/{admin_id}/application/{id}/decline")
    public void declineApplicationByAdmin(@PathVariable("id") Long applicationId,
                                            @PathVariable("admin_id") Long adminId,
                                            @RequestParam(value = "note", required = false) String note,
                                           HttpServletResponse response) throws IOException {
        try {
            UserEntity adminUser = dataProcessController.getUserWithRoleOrThrow(
                    adminId,
                    roleRepository.findById(RoleConstants.ADMIN).get());
            ApplicationEntity application = dataProcessController.getApplicationOrThrow(applicationId);
            dataProcessController.throwIfServiceNotOfAdmin(application.getService(), adminUser);

            ApplicationCheckingEntity checkRecord = null;
            //Дивимося записи перевірок, щоб зрозуміти, чи заявка очікує підтвердження від адміна
            for (ApplicationCheckingEntity check: application.getCheckings() ) {//перевіряємо записи власника і адміністратора
                if(check.getCheckType().equals(checkTypeRepository.findById(CheckTypeConstants.CHECKING_RECORD).get()) &&
                        check.getRole().equals(roleRepository.findById(RoleConstants.OWNER).get()) ){//якщо це запис власника
                    if(check.getCheckYesNoNull() == null){
                        throw new DataProcessException("Власник ще не перевірив заявку");
                    }else if(check.getCheckYesNoNull()){//і він підтверджений
                        //то все в порядку, ідемо далі
                    }else{
                        throw new DataProcessException("Власник відхилив заявку");
                    }
                } else if (check.getCheckType().equals(checkTypeRepository.findById(CheckTypeConstants.CHECKING_RECORD).get()) &&
                        check.getRole().equals(roleRepository.findById(RoleConstants.ADMIN).get()) ){//якщо це запис адміна
                    if(check.getCheckYesNoNull() == null){
                        //адмін ще не перевірив заявку
                        checkRecord = check;
                    }else if(check.getCheckYesNoNull()){//і він підтверджений
                        throw new DataProcessException("Адміністратор вже прийняв заявку");
                    }else{
                        throw new DataProcessException("Адміністратор вже відхилив заявку");
                    }
                }else{//це запис користувача, нічого не робимо
                    //збережемо на потім
                }
            }

            //Відхилюємо заявку
            //Адмін, що прийняв заявку
            checkRecord.setUser(adminUser);
            //Запис факту відхилення заявки
            checkRecord.setCheckYesNoNull(false);
            //Дата відхилення заявки
            checkRecord.setCheckingDate(new java.sql.Timestamp(new Date().getTime()));
            //Опціональний коментар
            if(note != null){
                checkRecord.setNote(note);
            }
            //Зберігаємо запис перевірки
            applicationCheckingRepository.saveAndFlush(checkRecord);

            response.sendRedirect("/admin/"+ adminId + "/application/refreshed");
            return;

        }catch (DataProcessException e) {
            e.printStackTrace();
            response.sendRedirect("/error");
            return;
        }
    }

    /**
     * Повертає всіх користувачів
     * @return Список користувачів
     */
    @GetMapping("/admin/user/all")
    public List<UserEntity> getAllUsers(){

        List<UserEntity> users = userRepository.findAll();
        return users;
    }

    /**
     * Повертає одного користувача
     * @param id Ідентифікатор користувача
     * @param response через цей об'єкт виконується редірект
     * @return Користувач або null
     * @throws IOException якщо будуть проблеми з редіректом
     */
    @GetMapping("/admin/user/{id}")
    public UserEntity getUser(@PathVariable("id") Long id,
                              HttpServletResponse response) throws IOException {
        try {
            UserEntity user = dataProcessController.getUserOrThrow(id);
            return user;

        } catch (DataProcessException e) {
            e.printStackTrace();
            response.sendRedirect("/error");
            return null;
        }
    }

    /**
     * Повертає усі заявки одного користувача
     * @param userId Ідентифікатор заявки
     * @param response через цей об'єкт виконується редірект
     * @return Список заявок або null
     * @throws IOException якщо будуть проблеми з редіректом
     */
    @GetMapping("/admin/user/{id}/application/all")
    public List<ApplicationEntity> getWorkerApplications(@PathVariable("id") Long userId,
                                                         HttpServletResponse response) throws IOException {
        try {
            UserEntity user = dataProcessController.getUserOrThrow(userId);

            List<ApplicationEntity> applications = applicationRepository.getAllByApplicant(user);
            return applications;

        } catch (DataProcessException e) {
            e.printStackTrace();
            response.sendRedirect("/error");
            return null;
        }
    }

    /**
     * Повертає робітників
     * @return Список робітників
     */
    @GetMapping("/admin/worker/all")
    public List<WorkerEntity> getAllWorkers(){

        List<WorkerEntity> workers = workerRepository.findAll();
        return workers;
    }

    /**
     * Повертає одного робітника
     * @param id Ідентифікатор робітника
     * @param response через цей об'єкт виконується редірект
     * @return Робітник або null
     * @throws IOException якщо будуть проблеми з редіректом
     */
    @GetMapping("/admin/worker/{id}")
    public WorkerEntity getWorker(@PathVariable("id") Long id,
                                  HttpServletResponse response) throws IOException {
        try {
            WorkerEntity worker = dataProcessController.getWorkerOrThrow(id);
            return worker;

        } catch (DataProcessException e) {
            e.printStackTrace();
            response.sendRedirect("/error");
            return null;
        }
    }

    /**
     * Повертає всі підрозділи
     * @return Список підрозділів
     */
    @GetMapping("/admin/department/all")
    public List<DepartmentEntity> getAllDepartments(){

        List<DepartmentEntity> departments = departmentRepository.findAll();
        return departments;
    }


    /**
     * Повертає всі посади
     * @return Список посад
     */
    @GetMapping("/admin/position/all")
    public List<PositionEntity> getAllPositions(){

        List<PositionEntity> positions = positionRepository.findAll();
        return positions;
    }



}
