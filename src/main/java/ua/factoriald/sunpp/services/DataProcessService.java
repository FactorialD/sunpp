package ua.factoriald.sunpp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ua.factoriald.sunpp.model.*;
import ua.factoriald.sunpp.model.constants.CheckTypeConstants;
import ua.factoriald.sunpp.model.constants.RoleConstants;
import ua.factoriald.sunpp.repository.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Клас зберігає методи, що працюють з даними з бази даних і опрацьовують їх для зручного користування
 *
 * У випадку проблем методи викидають помилку з кодом помилки.
 * Для правильного використання виклики цих методів потрібно завернути в блок try-catch
 * і прокидувати виключення далі.
 *
 */
@Component
public class DataProcessService {

    private final UserRepository userRepository;
    private final UserHaveAccessToServiceRepository accessRepository;
    private final RoleRepository roleRepository;
    private final ServiceRepository serviceRepository;
    private final ApplicationRepository applicationRepository;
    private final WorkerRepository workerRepository;
    private final DepartmentRepository departmentRepository;
    private final CheckTypeRepository checkTypeRepository;

    @Autowired
    public DataProcessService(UserRepository userRepository, UserHaveAccessToServiceRepository accessRepository, RoleRepository roleRepository, ServiceRepository serviceRepository, ApplicationRepository applicationRepository, WorkerRepository workerRepository, DepartmentRepository departmentRepository, CheckTypeRepository checkTypeRepository) {
        this.userRepository = userRepository;
        this.accessRepository = accessRepository;
        this.roleRepository = roleRepository;
        this.serviceRepository = serviceRepository;
        this.applicationRepository = applicationRepository;
        this.workerRepository = workerRepository;
        this.departmentRepository = departmentRepository;
        this.checkTypeRepository = checkTypeRepository;
    }

    /**
     * Повертає користувача за ідентифікатором, якщо він має потрібну роль
     * @param userId Ідентифікатор користувача
     * @param role Потрібна роль користувача
     * @return Користувач
     * @throws ResponseStatusException, якщо немає такого користувача чи у нього немає такої ролі
     */
    public UserEntity getUserWithRoleOrThrow(Long userId, RoleEntity role) throws ResponseStatusException {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if(!userOpt.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Немає такого користувача");
        }else{
            UserEntity user = userOpt.get();
            if(accessRepository.getAllByUserAndRole(user, role).size() == 0){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Цей користувач не має потрібних прав");
            }else{
                //Все в порядку, цей користувач існує і в нього є потрібні права
                return user;
            }
        }
    }

    /**
     * Повертає користувача за ідентифікатором
     * @param userId Ідентифікатор користувача
     * @return Користувач
     * @throws ResponseStatusException, якщо немає такого користувача
     */
    public UserEntity getUserOrThrow(Long userId) throws ResponseStatusException {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if(!userOpt.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Немає такого користувача");
        }else{
            return userOpt.get();
        }
    }

    /**
     * Повертає робітника за ідентифікатором
     * @param workerId Ідентифікатор робітника
     * @return Робітник
     * @throws ResponseStatusException, якщо немає такого робітника
     */
    public WorkerEntity getWorkerOrThrow(Long workerId) throws ResponseStatusException {
        Optional<WorkerEntity> workerOpt = workerRepository.findById(workerId);
        if(!workerOpt.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Немає такого працівника");
        }else{
            return workerOpt.get();
        }
    }

    /**
     * Повертає сервіс за ідентифікатором
     * @param serviceId Ідентифікатор сервісу
     * @return Сервіс
     * @throws ResponseStatusException, якшо немає такого сервісу
     */
    public ServiceEntity getServiceOrThrow(Long serviceId) throws ResponseStatusException {
        Optional<ServiceEntity> serviceOpt = serviceRepository.findById(serviceId);
        if(!serviceOpt.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Немає такого сервісу");
        }
        return serviceOpt.get();
    }

    /**
     * Повертає підрозділ за ідентифікатором
     * @param departmentId Ідентифікатор підрозділу
     * @return Підрозділ
     * @throws ResponseStatusException, якщо немає такого підрозділу
     */
    public DepartmentEntity getDepartmentOrThrow(Long departmentId) throws ResponseStatusException {
        Optional<DepartmentEntity> departmentOpt = departmentRepository.findById(departmentId);
        if(!departmentOpt.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Немає такого підрозділу");
        }
        return departmentOpt.get();
    }

    /**
     * Повертає роль за ідентифікатором
     * @param roleId Ідентифікатор ролі
     * @return Роль
     * @throws ResponseStatusException, якщо такої ролі немає
     */
    public RoleEntity getRoleOrThrow(Long roleId) throws ResponseStatusException {
        Optional<RoleEntity> roleOpt = roleRepository.findById(roleId);
        if(!roleOpt.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Немає такої ролі");
        }
        return roleOpt.get();
    }

    /**
     * Повертає заявку за ідентифікатором
     * @param applicationId Ідентифікатор заявки
     * @return Заявки
     * @throws ResponseStatusException, якщо такої заявки немає
     */
    public ApplicationEntity getApplicationOrThrow(Long applicationId) throws ResponseStatusException {
        Optional<ApplicationEntity> applicationOpt = applicationRepository.findById(applicationId);
        if(!applicationOpt.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Немає такої заявки");
        }
        return applicationOpt.get();
    }

    /**
     * Метод створений для того, щоб викидати ResponseStatusException, якщо сервіс не належить власнику
     * @param service Сервіс
     * @param user Потенціальний власник сервісу
     * @throws ResponseStatusException, якщо сервіс не належить власнику
     */
    public void throwIfServiceNotOfOwner(ServiceEntity service, UserEntity user) throws ResponseStatusException {

        List<ServiceEntity> ownerServices = serviceRepository.getAllByOwnerUser(user);
        if(!ownerServices.contains(service)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Це не сервіс власника");
        }
    }

    /**
     * Метод створений для того, щоб викидати ResponseStatusException, якщо сервіс не належить адміністратору
     * @param service Сервіс
     * @param user Потенціальний адміністратор сервісу
     * @throws ResponseStatusException, якщо сервіс не належить адміністратору
     */
    public void throwIfServiceNotOfAdmin(ServiceEntity service, UserEntity user) throws ResponseStatusException {

        List<UserHaveAccessToServiceEntity> accesses = accessRepository.getAllByUserAndRole(
                user,
                roleRepository.findById(RoleConstants.ADMIN).get());
        for (UserHaveAccessToServiceEntity access: accesses
             ) {
            if(access.getService().equals(service)){
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Це не сервіс адміністратора");
    }

    /**
     * Повертає список заявок, які не перевірялись власником
     * @param allApplications Початковий список заявок, з якого будуть вибиратися потрібні
     * @return Список заявок
     */
    public List<ApplicationEntity> getRefreshedApplicationsForOwner(List<ApplicationEntity> allApplications){

        List<ApplicationEntity> refreshedApplications = new ArrayList<>();

        for (ApplicationEntity app: allApplications) {//дивимося на заявки
            for (ApplicationCheckingEntity check: app.getCheckings() ) {//дивимося на записи заявок
                if (check.getCheckType().equals(checkTypeRepository.findById(CheckTypeConstants.CHECKING_RECORD).get()) &&
                        check.getRole().equals(roleRepository.findById(RoleConstants.OWNER).get()) &&
                        check.getCheckYesNoNull() == null ){//якщо це запис потрібної ролі, і він не перевірений
                    refreshedApplications.add(app);
                    break;
                }
            }
        }
        return refreshedApplications;
    }

    /**
     * Повертає усі заявки, що стосуються адміністратора (заявки по сервісам адміністратора)
     * @param admin Адміністратор
     * @return Список заявок
     */
    public List<ApplicationEntity> getAllApplicationsForAdmin(UserEntity admin){
        List<UserHaveAccessToServiceEntity> accesses = accessRepository.getAllByUserAndRole(admin,
                roleRepository.findById(RoleConstants.ADMIN).get()
        );
        List<ApplicationEntity> applications = new ArrayList<>();
        for (UserHaveAccessToServiceEntity access: accesses) {
            applications.addAll(applicationRepository.getAllByService(access.getService()));
        }
        return applications;
    }

    /**
     * Повертає список заявок, які перевірялись власником, але не перевірялись адміністратором
     * @param allApplications Список заявок, з яких вибираються потрібні
     * @return Список заявок
     */
    public List<ApplicationEntity> getRefreshedApplicationsForAdmin(List<ApplicationEntity> allApplications){

        List<ApplicationEntity> refreshedApplications = new ArrayList<>();

        for (ApplicationEntity app: allApplications) {//дивимося на заявки
            boolean checkedByOwner = false;
            boolean notCheckedByAdmin = false;
            for (ApplicationCheckingEntity check: app.getCheckings() ) {//дивимося на записи заявок
                //спочатку йде перевірка, що включає перевірку на null, бо в іншому випадку може бути NullPointerException
                if (check.getRole().equals(roleRepository.findById(RoleConstants.ADMIN).get()) &&
                        check.getCheckYesNoNull() == null) {//якщо адмін не перевіряв заявку
                    //позначаємо цю заявку
                    notCheckedByAdmin = true;
                } else if (check.getCheckType().equals(checkTypeRepository.findById(CheckTypeConstants.CHECKING_RECORD).get()) &&
                        check.getRole().equals(roleRepository.findById(RoleConstants.OWNER).get()) &&
                        check.getCheckYesNoNull() != null && check.getCheckYesNoNull()) {//якщо власник прийняв заявку
                    //позначаємо цю заявку
                    checkedByOwner = true;
                }
            }
            if(checkedByOwner && notCheckedByAdmin){//якщо заявка перевірена власником і не перевірена адміном
                refreshedApplications.add(app);
            }
        }
        return refreshedApplications;
    }

    /**
     * Перевіряє записи заявки і якщо вона готова до перевірки адміном, то повертає записи перевірок
     * @param application заявка, яка перевіряється
     * @return Хеш-мап, що зберігає в собі запис перевірки адміна і запис перевірки користувача
     */
    public HashMap<String, ApplicationCheckingEntity> checkApplicationReadyForAdminAndGetCheckingsOrThrow(ApplicationEntity application)
            throws ResponseStatusException{
        ApplicationCheckingEntity adminCheckRecord = null;
        ApplicationCheckingEntity userCheckRecord = null;

        //Дивимося записи перевірок, щоб зрозуміти, чи заявка очікує підтвердження від адміна
        for (ApplicationCheckingEntity check: application.getCheckings() ) {//перевіряємо записи власника і адміністратора
            if(check.getCheckType().equals(checkTypeRepository.findById(CheckTypeConstants.CHECKING_RECORD).get()) &&
                    check.getRole().equals(roleRepository.findById(RoleConstants.OWNER).get()) ){//якщо це запис власника
                if(check.getCheckYesNoNull() == null){
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Власник ще не перевірив заявку");
                }else if(check.getCheckYesNoNull()){//і він підтверджений
                    //то все в порядку, ідемо далі
                }else{
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Власник відхилив заявку");
                }
            } else if (check.getCheckType().equals(checkTypeRepository.findById(CheckTypeConstants.CHECKING_RECORD).get()) &&
                    check.getRole().equals(roleRepository.findById(RoleConstants.ADMIN).get()) ){//якщо це запис адміна
                if(check.getCheckYesNoNull() == null){
                    //адмін ще не перевірив заявку
                    adminCheckRecord = check;
                }else if(check.getCheckYesNoNull()){//і він підтверджений
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Адміністратор вже прийняв заявку");
                }else{
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Адміністратор вже відхилив заявку");
                }
            }else{//це запис користувача, нічого не робимо
                //збережемо на потім
                userCheckRecord = check;
            }
        }

        HashMap<String, ApplicationCheckingEntity> checkings = new HashMap<>();
        checkings.put("userCheckRecord", userCheckRecord);
        checkings.put("adminCheckRecord", adminCheckRecord);
        return checkings;

    }

    /**
     * Перевіряє записи заявки і якщо вона готова до перевірки власником, то повертає запис перевірки
     * @param application заявка, яка перевіряється
     * @return запис перевірки адміна і запис перевірки користувача
     */
    public ApplicationCheckingEntity checkApplicationReadyForOwnerAndGetCheckingOrThrow(ApplicationEntity application)
            throws ResponseStatusException{
        ApplicationCheckingEntity ownerCheckRecord = null;
        //Дивимося записи перевірок, щоб зрозуміти, чи заявка очікує підтвердження від власника
        for (ApplicationCheckingEntity check: application.getCheckings() ) {//перевіряємо запис власника
            if(check.getCheckType().equals(checkTypeRepository.findById(CheckTypeConstants.CHECKING_RECORD).get()) &&
                    check.getRole().equals(roleRepository.findById(RoleConstants.OWNER).get()) ){//якщо це запис власника
                if(check.getCheckYesNoNull() == null){
                    //все в порядку, ідемо далі
                    ownerCheckRecord = check;
                }else if(check.getCheckYesNoNull()){//і він підтверджений
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Власник вже перевірив заявку");
                }else{
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Власник вже відхилив заявку");
                }
            }
        }
        return ownerCheckRecord;
    }

    /**
     * Намагається конвертувати строку в число. Якщо це null чи не число, то кидає виключення
     * @param string
     * @return
     * @throws ResponseStatusException
     */
    public Long getLongOrThrow(String string) throws ResponseStatusException {
        try {
            //Long.valueOf кидає виключення не тільки коли строка не конвертується в число, а і якщо строка є null
            return Long.valueOf(string);

        } catch(NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Неправильні дані");
        }
    }

}
