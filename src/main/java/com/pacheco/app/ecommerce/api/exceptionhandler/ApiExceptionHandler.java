package com.pacheco.app.ecommerce.api.exceptionhandler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pacheco.app.ecommerce.domain.exception.BusinessException;
import com.pacheco.app.ecommerce.domain.exception.EntityUsedException;
import com.pacheco.app.ecommerce.domain.exception.NotFoundEntityException;
import com.pacheco.app.ecommerce.infrastructure.email.EmailFactory;
import com.pacheco.app.ecommerce.infrastructure.email.EmailService;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String MSG_ERRO_GENERICO_USUARIO_FINAL
            = "Ocurred an unexpected internal error on system. Try again, " +
            "if the problem persists, contact the system administrator.";

    public static final String METHOD_ARGUMENT_TYPE_MISMATCH_MSG
            = "The Url parameter '%s' has received '%s', that is of a invalid type. " +
            "Correct and inform a compatible with the type %s";

    public static final String INVALID_FORMAT_MSG
            = "The property '%s' has received '%s', that is of a invalid type. " +
            "Correct and inform a compatible with the type %s";

    public static final String PROPERTY_BINDING_MSG = "The property '%s' is invalid.";

    public static final String INVALID_DATA_MSG = "Some values of the parameters are invalid";

    public static final String HTTP_MESSAGE_NOT_READABLE_MSG
            = "The requisition body is invalid. Verify syntax errors.";

    public static final String RESOURCE_NOT_FOUND_MSG = "The resource '%s', that you have tried to access, do not exist.";

    public static final String MSSING_REQUEST_PART_MSG = "The part '%s' are missing in the request";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailFactory emailFactory;

    @Autowired
    private Environment environment;

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        ProblemType type = ProblemType.RESOURCE_NOT_FOUND;
        String detail = String.format(RESOURCE_NOT_FOUND_MSG, ex.getRequestURL());
        Problem problem = createProblemBuilder(status, type, detail).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(
            MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        ProblemType type = ProblemType.MISSING_REQUEST_PART;
        String detail = String.format(MSSING_REQUEST_PART_MSG, ex.getRequestPartName());
        Problem problem = createProblemBuilder(status, type, detail).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        Throwable rootCause = ExceptionUtils.getRootCause(ex);

        if (rootCause instanceof InvalidFormatException) {
            return handleInvalidFormatException((InvalidFormatException) rootCause, headers, status, request);
        } else if (rootCause instanceof PropertyBindingException) {
            return handlePropertyBindingException((PropertyBindingException) rootCause, headers, status, request);
        }

        ProblemType type = ProblemType.INCOMPREHENSIBLE_MESSAGE;
        String detail = HTTP_MESSAGE_NOT_READABLE_MSG;
        Problem problem = createProblemBuilder(status, type, detail).build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleInvalidData(ex, status, request, ex.getBindingResult().getAllErrors());
    }

    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleInvalidData(ex, status, request, ex.getBindingResult().getAllErrors());
    }

    private ResponseEntity<Object> handleInvalidData(
            Exception ex, HttpStatus status, WebRequest request, List<ObjectError> errors) {

        ProblemType type = ProblemType.INVALID_DATA;
        String detail = INVALID_DATA_MSG;

        List<Problem.Object> problemObjects = errors.stream()
                .map(objectError -> {
                    String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
                    String name = objectError.getObjectName();

                    if (objectError instanceof FieldError) {
                        name = ((FieldError) objectError).getField();
                    }

                    return Problem.Object.builder()
                            .name(name)
                            .userMessage(message)
                            .build();
                })
                .collect(Collectors.toList());

        Problem problem = createProblemBuilder(status, type, detail)
                .userMessage(detail)
                .objects(problemObjects)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(PropertyBindingException.class)
    private ResponseEntity<Object> handlePropertyBindingException(
            PropertyBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String path = ex.getPath().stream()
                .map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.joining("."));

        ProblemType problemType = ProblemType.INCOMPREHENSIBLE_MESSAGE;
        String detail = String.format(PROPERTY_BINDING_MSG, path);
        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(MSG_ERRO_GENERICO_USUARIO_FINAL)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @ExceptionHandler(InvalidFormatException.class)
    private ResponseEntity<Object> handleInvalidFormatException(
            InvalidFormatException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String path = ex.getPath().stream()
                .map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.joining("."));

        ProblemType problemType = ProblemType.INCOMPREHENSIBLE_MESSAGE;
        String detail = String.format(INVALID_FORMAT_MSG, path, ex.getValue(), ex.getTargetType());
        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(MSG_ERRO_GENERICO_USUARIO_FINAL)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemType type = ProblemType.INVALID_PARAM;
        String detail = String.format(METHOD_ARGUMENT_TYPE_MISMATCH_MSG,
                e.getParameter(), e.getValue(), e.getRequiredType());
        Problem problem = createProblemBuilder(status, type, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(e, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(NotFoundEntityException.class)
    public ResponseEntity<?> handleNotFoundEntityException(NotFoundEntityException e, WebRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemType type = ProblemType.RESOURCE_NOT_FOUND;
        String detail = e.getMessage();
        Problem problem = createProblemBuilder(status, type, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(e, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(EntityUsedException.class)
    public ResponseEntity<?> handleEntityUsedException(EntityUsedException e, WebRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;
        ProblemType type = ProblemType.ENTITY_USED;
        String detail = e.getMessage();
        Problem problem = createProblemBuilder(status, type, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(e, problem, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException e, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemType problemType = ProblemType.BUSINESS_ERROR;
        String detail = e.getMessage();

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(e, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleExceptions(Exception e, WebRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemType type = ProblemType.INTERNAL_ERROR;
        String detail = MSG_ERRO_GENERICO_USUARIO_FINAL;
        Problem problem = createProblemBuilder(status, type, detail)
                .userMessage(detail)
                .build();

        e.printStackTrace();
        sendEmailIfInProduction(e);

        return handleExceptionInternal(e, problem, new HttpHeaders(), status, request);
    }

    /* SECURITY EXCEPTION HANDLERS */

    public void handleAuthenticationError(String message,
                                          HttpServletResponse response) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ProblemType type = ProblemType.AUTHENTICATION_ERROR;
        String detail = message;
        Problem problem = createProblemBuilder(status, type, detail)
                .userMessage(detail)
                .build();

        handleSecurityExceptionInternal(problem, response);
    }

    private void handleSecurityExceptionInternal(Problem problem, HttpServletResponse response) {
        response.setStatus(problem.getStatus());
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            String problemJson = objectMapper.writeValueAsString(problem);

            PrintWriter printWriter = response.getWriter();
            printWriter.println(problemJson);
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*******************************/


    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {

        if (body == null) {
            body = Problem.builder()
                    .timestamp(LocalDateTime.now())
                    .title(status.getReasonPhrase())
                    .status(status.value())
                    .build();
        } else if (body instanceof String) {
            body = Problem.builder()
                    .timestamp(LocalDateTime.now())
                    .title((String) body)
                    .status(status.value())
                    .build();
        }

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    private void sendEmailIfInProduction(Exception e) {
        if (List.of(environment.getActiveProfiles()).contains("prod")) {
            emailService.sendEmail(emailFactory.createInternalErrorEmail(e));
        }
    }

    public Problem.ProblemBuilder createProblemBuilder(HttpStatus status, ProblemType type, String detail) {
        return Problem.builder()
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .title(type.getTitle())
                .detail(detail);
    }
}
