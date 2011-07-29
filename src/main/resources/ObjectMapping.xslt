<?xml version="1.0" encoding="utf-8"?>
<!-- This stylesheet fills the rule skeleton with names. It copies everything and uses
		templates to define exceptions where something should be changed -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8"
		indent="yes"/>
	<!-- Document that contains the names -->
	<xsl:variable name="names"
		select="document('Names.xml')/names" />
	<!-- copy everything -->
	<xsl:template match="@* | node()">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()" />
		</xsl:copy>
	</xsl:template>
	<!-- Exception for the package name attribute -->
	<xsl:template match="package">
		<xsl:copy>
			<xsl:attribute name="name">
    			<xsl:value-of select="$names/package/@name" disable-output-escaping="yes"></xsl:value-of>
    		</xsl:attribute>
			<!-- Import statements -->
			<xsl:for-each select="$names/imports/import">
				<xsl:variable name="importposition">
					<xsl:number />
				</xsl:variable>
				<xsl:element name="import">
					<xsl:attribute name="name">
	    				<xsl:value-of
						select="$names/imports/import[position()=$importposition]/@name"></xsl:value-of>
	    			</xsl:attribute>
				</xsl:element>
			</xsl:for-each>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	<!-- Exception for the rule element -->
	<xsl:template match="rule">
		<xsl:variable name="position">
			<xsl:number />
		</xsl:variable>
		<!-- copy the rule element -->
		<xsl:copy>
			<!-- add a new attribute name -->
			<xsl:attribute name="name">
	      	<xsl:value-of select="$names/rules/rule[position()=$position]/@name" />
	    </xsl:attribute>
			<!-- continue with the copying -->
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	<!-- Exception for field-constraints -->
	<xsl:template match="field-constraint/@field-name">
		<xsl:variable name="field-name">
			<xsl:value-of select="."></xsl:value-of>
		</xsl:variable>
		<!-- change attribute name -->
		<xsl:attribute name="field-name">
		    <xsl:value-of
			select="$names//field[@name=$field-name]/@attribute" />
		 </xsl:attribute>
	</xsl:template>
	<!-- Exception to give pattern the right object type -->
	<xsl:template match="pattern">
		<xsl:variable name="field-name">
			<xsl:value-of select="field-constraint/@field-name" />
		</xsl:variable>
		<xsl:copy>
			<!-- add object-type attribute -->
			<xsl:attribute name="object-type">
				<!-- <xsl:for-each select="$names/objects/object">
					<xsl:for-each select="field">
						<xsl:if test="@name=$field-name">
							<xsl:text>variable :</xsl:text>
							<xsl:value-of select="../@name"></xsl:value-of>
						</xsl:if>
					</xsl:for-each>
				</xsl:for-each> -->
				<xsl:text>variable :</xsl:text>
				<xsl:value-of select="$names/object/@name" />
			</xsl:attribute>
			<!-- continue with the copying -->
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	<!-- Exception for rhs to replace the name of the field if necessary -->
	<xsl:template match="rhs">
		<!-- Retrieve the name of the field that is used for the classification -->
		<xsl:variable name="oldClass" select="substring-before(substring-after(.,'modify(variable) {'),'=')"/>
		<!-- Retrieve the name of the corresponding attribute in the names.xml -->
		<xsl:variable name="newClass" select="$names/object/field[@name=$oldClass]/@attribute"></xsl:variable>
		<!-- Copy everything and replace the name. -->
		<xsl:copy>
			<xsl:value-of select="replace(.,$oldClass,$newClass)"/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>