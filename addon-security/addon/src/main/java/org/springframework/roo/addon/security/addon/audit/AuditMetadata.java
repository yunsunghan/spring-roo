package org.springframework.roo.addon.security.addon.audit;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.roo.addon.security.annotations.RooSecurityConfiguration;
import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.*;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.classpath.itd.AbstractItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.classpath.itd.InvocableMemberBodyBuilder;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.model.*;
import org.springframework.roo.project.LogicalPath;

/**
 * Metadata for {@link RooSecurityConfiguration}.
 * <p>
 * 
 * @author Sergio Clares
 * @since 2.0
 */
public class AuditMetadata extends AbstractItdTypeDetailsProvidingMetadataItem {

  private static final String PROVIDES_TYPE_STRING = AuditMetadata.class.getName();
  private static final String PROVIDES_TYPE = MetadataIdentificationUtils
      .create(PROVIDES_TYPE_STRING);

  private AuditAnnotationValues annotationValues;

  public static String createIdentifier(final JavaType javaType, final LogicalPath path) {
    return PhysicalTypeIdentifierNamingUtils.createIdentifier(PROVIDES_TYPE_STRING, javaType, path);
  }

  public static JavaType getJavaType(final String metadataIdentificationString) {
    return PhysicalTypeIdentifierNamingUtils.getJavaType(PROVIDES_TYPE_STRING,
        metadataIdentificationString);
  }

  public static String getMetadataIdentiferType() {
    return PROVIDES_TYPE;
  }

  public static LogicalPath getPath(final String metadataIdentificationString) {
    return PhysicalTypeIdentifierNamingUtils.getPath(PROVIDES_TYPE_STRING,
        metadataIdentificationString);
  }

  public static boolean isValid(final String metadataIdentificationString) {
    return PhysicalTypeIdentifierNamingUtils.isValid(PROVIDES_TYPE_STRING,
        metadataIdentificationString);
  }

  /**
   * Constructor
   * 
   * @param identifier
   * @param aspectName
   * @param governorPhysicalTypeMetadata
   */
  public AuditMetadata(final String identifier, final JavaType aspectName,
      final PhysicalTypeMetadata governorPhysicalTypeMetadata,
      final AuditAnnotationValues annotationValues) {
    super(identifier, aspectName, governorPhysicalTypeMetadata);
    Validate
        .isTrue(
            isValid(identifier),
            "Metadata identification string '%s' does not appear to be a valid physical type identifier",
            identifier);

    this.annotationValues = annotationValues;

    // Add audit fields
    FieldMetadata createdDateField = getCreatedDateField();
    builder.addField(createdDateField);
    FieldMetadata modifiedDateField = getModifiedDateField();
    builder.addField(modifiedDateField);
    FieldMetadata createdByField = getCreatedByField();
    builder.addField(createdByField);
    FieldMetadata modifiedByField = getModifiedByField();
    builder.addField(modifiedByField);

    // Add getters for audit fields
    builder.addMethod(getDeclaredGetter(createdDateField));
    builder.addMethod(getDeclaredGetter(modifiedDateField));
    builder.addMethod(getDeclaredGetter(createdByField));
    builder.addMethod(getDeclaredGetter(modifiedByField));

    // Add @EntityListeners annotation
    builder.addAnnotation(getEntityListenersAnnotation());

    // Build ITD
    itdTypeDetails = builder.build();
  }

  /**
     * Builds createdDate field for storing entity's created date 
     * 
     * @return FieldMetadataBuilder for building field in ITD
     */
  private FieldMetadata getCreatedDateField() {

    // Create field annotations
    List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Only add @Column if required by annotation @RooAudit
    if (StringUtils.isNotBlank(this.annotationValues.getCreatedDateColumn())) {
      AnnotationMetadataBuilder columnAnnotation =
          new AnnotationMetadataBuilder(JpaJavaType.COLUMN);
      columnAnnotation.addStringAttribute("name", this.annotationValues.getCreatedDateColumn());
      annotations.add(columnAnnotation);
      builder.getImportRegistrationResolver().addImports(JpaJavaType.COLUMN);
    }

    AnnotationMetadataBuilder createdDateAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.CREATED_DATE);
    annotations.add(createdDateAnnotation);
    AnnotationMetadataBuilder temporalAnnotation =
        new AnnotationMetadataBuilder(JpaJavaType.TEMPORAL);
    temporalAnnotation.addEnumAttribute("value", new EnumDetails(JpaJavaType.TEMPORAL_TYPE,
        new JavaSymbolName("TIMESTAMP")));
    annotations.add(temporalAnnotation);


    // Add imports
    builder.getImportRegistrationResolver().addImports(SpringJavaType.CREATED_DATE,
        JpaJavaType.TEMPORAL);

    // Create field
    FieldMetadataBuilder fieldBuilder =
        new FieldMetadataBuilder(getId(), Modifier.PRIVATE, annotations, new JavaSymbolName(
            "createdDate"), JdkJavaType.CALENDAR);

    return fieldBuilder.build();
  }

  /**
   * Builds modifiedDate field for storing entity's last modified date 
   * 
   * @return FieldMetadataBuilder for building field in ITD
   */
  private FieldMetadata getModifiedDateField() {

    // Create field annotations
    List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Only add @Column if required by annotation @RooAudit
    if (StringUtils.isNotBlank(this.annotationValues.getModifiedDateColumn())) {
      AnnotationMetadataBuilder columnAnnotation =
          new AnnotationMetadataBuilder(JpaJavaType.COLUMN);
      columnAnnotation.addStringAttribute("name", this.annotationValues.getModifiedDateColumn());
      annotations.add(columnAnnotation);
      builder.getImportRegistrationResolver().addImports(JpaJavaType.COLUMN);
    }

    AnnotationMetadataBuilder createdDateAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.LAST_MODIFIED_DATE);
    annotations.add(createdDateAnnotation);
    AnnotationMetadataBuilder temporalAnnotation =
        new AnnotationMetadataBuilder(JpaJavaType.TEMPORAL);
    temporalAnnotation.addEnumAttribute("value", new EnumDetails(JpaJavaType.TEMPORAL_TYPE,
        new JavaSymbolName("TIMESTAMP")));
    annotations.add(temporalAnnotation);


    // Add imports
    builder.getImportRegistrationResolver().addImports(SpringJavaType.LAST_MODIFIED_DATE,
        JpaJavaType.TEMPORAL);

    // Create field
    FieldMetadataBuilder fieldBuilder =
        new FieldMetadataBuilder(getId(), Modifier.PRIVATE, annotations, new JavaSymbolName(
            "modifiedDate"), JdkJavaType.CALENDAR);

    return fieldBuilder.build();
  }

  /**
   * Builds createdBy field for storing user who creates entity registers
   * 
   * @return FieldMetadataBuilder for building field in ITD
   */
  private FieldMetadata getCreatedByField() {

    // Create field annotations
    List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Only add @Column if required by annotation @RooAudit
    if (StringUtils.isNotBlank(this.annotationValues.getCreatedByColumn())) {
      AnnotationMetadataBuilder columnAnnotation =
          new AnnotationMetadataBuilder(JpaJavaType.COLUMN);
      columnAnnotation.addStringAttribute("name", this.annotationValues.getCreatedByColumn());
      builder.getImportRegistrationResolver().addImports(JpaJavaType.COLUMN);
      annotations.add(columnAnnotation);
    }

    AnnotationMetadataBuilder createdDateAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.CREATED_BY);
    annotations.add(createdDateAnnotation);

    // Add imports
    builder.getImportRegistrationResolver().addImports(SpringJavaType.CREATED_BY);

    // Create field
    FieldMetadataBuilder fieldBuilder =
        new FieldMetadataBuilder(getId(), Modifier.PRIVATE, annotations, new JavaSymbolName(
            "createdBy"), JavaType.STRING);

    return fieldBuilder.build();
  }

  /**
   * Builds modifiedBy field for storing user who last modifies entity registers
   * 
   * @return FieldMetadataBuilder for building field in ITD
   */
  private FieldMetadata getModifiedByField() {

    // Create field annotations
    List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Only add @Column if required by annotation @RooAudit
    if (StringUtils.isNotBlank(this.annotationValues.getModifiedByColumn())) {
      AnnotationMetadataBuilder columnAnnotation =
          new AnnotationMetadataBuilder(JpaJavaType.COLUMN);
      columnAnnotation.addStringAttribute("name", this.annotationValues.getModifiedByColumn());
      builder.getImportRegistrationResolver().addImports(JpaJavaType.COLUMN);
      annotations.add(columnAnnotation);
    }

    AnnotationMetadataBuilder createdDateAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.LAST_MODIFIED_BY);
    annotations.add(createdDateAnnotation);

    // Add imports
    builder.getImportRegistrationResolver().addImports(SpringJavaType.LAST_MODIFIED_BY);

    // Create field
    FieldMetadataBuilder fieldBuilder =
        new FieldMetadataBuilder(getId(), Modifier.PRIVATE, annotations, new JavaSymbolName(
            "modifiedBy"), JavaType.STRING);

    return fieldBuilder.build();
  }

  /**
   * Builds @EntityListeners annotation
   * 
   * @return AnnotationMetadataBuilder with the prepared annotation
   */
  private AnnotationMetadataBuilder getEntityListenersAnnotation() {
    AnnotationMetadataBuilder annotation =
        new AnnotationMetadataBuilder(JpaJavaType.ENTITY_LISTENERS);
    annotation.addClassAttribute("value", SpringJavaType.AUDITING_ENTITY_LISTENER);

    // Add imports
    builder.getImportRegistrationResolver().addImports(JpaJavaType.ENTITY_LISTENERS,
        SpringJavaType.AUDITING_ENTITY_LISTENER);

    return annotation;
  }

  /**
   * Obtains the specific accessor method contained within this ITD.
   * 
   * @param field
   *            that already exists on the type either directly or via
   *            introduction (required; must be declared by this type to be
   *            located)
   * @return the method corresponding to an accessor, or null if not found
   */
  private MethodMetadataBuilder getDeclaredGetter(final FieldMetadata field) {
    Validate.notNull(field, "Field required");

    // Compute the mutator method name
    final JavaSymbolName methodName = BeanInfoUtils.getAccessorMethodName(field);

    // See if the type itself declared the accessor
    if (governorHasMethod(methodName)) {
      return null;
    }

    // Decide whether we need to produce the accessor method
    if (!Modifier.isTransient(field.getModifier()) && !Modifier.isStatic(field.getModifier())) {
      final InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
      bodyBuilder.appendFormalLine("return this." + field.getFieldName().getSymbolName() + ";");

      return new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, field.getFieldType(),
          bodyBuilder);
    }

    return null;
  }
}
