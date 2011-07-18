<?xml version="1.0" encoding="utf-8"?>
<!-- This stylesheet adds all namespaces required by Drools and copies the rest of the given document -->
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0"
  xmlns="http://drools.org/drools-5.0" 
  xmlns:xs="http://www.w3.org/2001/XMLSchema-instance" 
  xs:schemaLocation="http://drools.org/drools-5.0 drools-4.0.xsd">
  <xsl:output method="xml" version="1.0" encoding="UTF-8"
		indent="yes"/>
  <xsl:template match="*">
    <xsl:element name="{local-name()}">
      <xsl:apply-templates select="@* | node()"/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="/*">
  	<xsl:element name="{local-name()}">
  		<xsl:attribute name="xs:schemaLocation">http://drools.org/drools-5.0 drools-4.0.xsd</xsl:attribute>
  		<xsl:apply-templates select="@* |node()"/>
  	</xsl:element>
  </xsl:template>
  <xsl:template match="@*">
  	<xsl:copy></xsl:copy>
  </xsl:template>

</xsl:stylesheet>

