<?xml version="1.0" encoding="UTF-8"?>
<!-- This stylesheet creates a Drools rule skeleton from a PMML decision tree -->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8"
		indent="yes">
	</xsl:output>
	<xsl:template match="/">
		<xsl:element name="package">
			<!-- Select the sequence of all leaf nodes (the nodes that don't have 
				child nodes) -->
			<xsl:apply-templates select="//Node[count(descendant::Node)=0]"></xsl:apply-templates>
		</xsl:element>
	</xsl:template>
	<!-- Start with the leaf nodes, which can be found by searching for rules that have zero decendants -->
	<xsl:template match="//Node[count(descendant::Node)=0]">
		<xsl:element name="rule">
			<!-- Classification rules should be executed only once, hence lock-on-active -->
			<xsl:element name="rule-attribute">
				<xsl:attribute name="name">
					<xsl:text>lock-on-active</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="value">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:element>
			<xsl:element name="lhs">
				<xsl:element name="pattern">
					<!-- For each node on the path extract the field, evaluator and value -->
					<xsl:for-each select="ancestor-or-self::Node[count(parent::Node)=1]">
						<!-- Include options for definitions with and without compound predicate,
								to support different applications -->
						<xsl:element name="field-constraint">
							<xsl:attribute name="field-name">
									<xsl:value-of select="SimplePredicate/@field" />
						  			<xsl:value-of select="CompoundPredicate/SimplePredicate[1]/@field" />
						  		</xsl:attribute>
							<xsl:element name="literal-restriction">
								<xsl:attribute name="evaluator">
						  				<xsl:variable name="return">
						  					<xsl:call-template name="replaceOperator">
						  						<xsl:with-param name="operator"
									select="SimplePredicate/@operator"></xsl:with-param>
						  					</xsl:call-template>
						  					<xsl:call-template name="replaceOperator">
						  						<xsl:with-param name="operator"
									select="CompoundPredicate/SimplePredicate[1]/@operator"></xsl:with-param>
						  					</xsl:call-template>
						  				</xsl:variable>
						  				<xsl:value-of select="$return"></xsl:value-of>
						  			</xsl:attribute>
								<xsl:attribute name="value">
						  				<xsl:value-of select="SimplePredicate/@value" />
						  				<xsl:value-of select="CompoundPredicate/SimplePredicate[1]/@value" />
						  			</xsl:attribute>
							</xsl:element>
						</xsl:element>
					</xsl:for-each>
				</xsl:element>
			</xsl:element>
			<xsl:element name="rhs">
				<xsl:text>modify(variable) {</xsl:text>
				<!-- Get the predicted variable -->
				<xsl:value-of select="//MiningField[@usageType='predicted']/@name"></xsl:value-of>
				<xsl:text>="</xsl:text>
				<!-- Get the predicted value -->
				<xsl:for-each select="ScoreDistribution">
					<!-- Sort by record count. The predicted value is the one that is defining 
						the node -->
					<xsl:sort select="@recordCount" data-type="number" order="descending" />
					<!-- After sorting the topmost position contains the desired value -->
					<xsl:if test="position() = 1">
						<xsl:value-of select="@value"></xsl:value-of>
					</xsl:if>
				</xsl:for-each>
				<xsl:text>"};</xsl:text>
				<xsl:text>
				</xsl:text>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<!-- template to replace an operator with the encoding expected by Drools -->
	<xsl:template name="replaceOperator">
		<xsl:param name="operator"></xsl:param>
		<xsl:choose>
			<xsl:when test="$operator='greaterThan'">
				<xsl:text>&gt;</xsl:text>
			</xsl:when>
			<xsl:when test="$operator='greaterOrEqual'">
				<xsl:text>&gt;=</xsl:text>
			</xsl:when>
			<xsl:when test="$operator='lessThan'">
				<xsl:text>&lt;</xsl:text>
			</xsl:when>
			<xsl:when test="$operator='lessOrEqual'">
				<xsl:text>&lt;=</xsl:text>
			</xsl:when>
			<xsl:when test="$operator='equal'">
				<xsl:text>==</xsl:text>
			</xsl:when>
			<xsl:when test="$operator='notEqual'">
				<xsl:text>!=</xsl:text>
			</xsl:when>
			<!-- For now operators that don't belong to the list above are copied without replacement -->
			<xsl:otherwise>
				<xsl:value-of select="$operator"></xsl:value-of>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>