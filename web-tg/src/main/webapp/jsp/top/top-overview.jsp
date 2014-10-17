<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<s:set value="overviewSupport" var="overviewSupport"/>
<s:set value="headerSupport" var="headerSupport"/>
<s:set value="tagSupport" var="tagSupport"/>
<s:set value="#overviewSupport.overviewObject" var="obj"/>
<s:set value="#overviewSupport.getTitle(#obj)" var="title"/>
<s:set value="#tagSupport.getTags(#obj)" var="tags"/>
<div class="col-xs-24 top-col">
	<s:set value="mediaProvider" var="mediaProvider"/>
	<s:set value="#mediaProvider.currentMedia" var="currentMedia"/>
	<div class="hidden-sm hidden-md hidden-lg ov-image-title">
   		<s:property value="#overviewSupport.getTitle(#obj)"/>
   	</div>
	<s:if test="#mediaProvider.getMedia() == null || #mediaProvider.getMedia().isEmpty()">
		<div class="<s:property value="#headerSupport.getPageStyle()"/>-filler">
		  	<div class="overview-image">
		  		<div class="overview-image-filler"></div>
		  	</div>
	  	</div>
	</s:if><s:else>
		<div id="photo-carousel" class="carousel slide" data-ride="carousel">
		  <!-- Indicators -->
		  <ol class="carousel-indicators">
		  	<s:set value="0" var="slide"/>
		  	<s:iterator value="#mediaProvider.getMedia()" var="media">
		    	<li data-target="#photo-carousel" data-slide-to="<s:property value='#slide'/>" class="<s:property value='#slide==0 ? "active" : ""'/>"></li>
		    	<s:set value="#slide+1" var="slide"/>
		    </s:iterator>
		  </ol>
		
		  <!-- Wrapper for slides -->
		  <div class="carousel-inner <s:property value="#headerSupport.getPageStyle()"/>-filler">

		  <s:set value="'active'" var="itemClass"/> 
		  <s:set value="0" var="imageIndex"/>
		  <s:iterator value="#mediaProvider.getMedia()" var="media">
		    <div class="item <s:property value='#itemClass'/> overview-image">
		    	<s:set value="''" var="itemClass"/>
		      	<s:if test="#media!=null">
					<div class="<s:property value="#media.width>#media.height?'ov-image ov-cover':'ov-image'"/> image-<s:property value="#imageIndex"/>"></div>
					<s:set value="#imageIndex+1" var="imageIndex"/>
				</s:if><s:else>
					<div class="overview-image-filler"></div>
				</s:else>
				
		      <div class="carousel-caption">
		         <s:if test="rightsManagementService.canDelete(#currentUser,#media)"> 
<%-- 						Adding the action toolbox on every image --%>
					<div class="media-toolbox">
						<a class="img-button" href="<s:property value="mediaProvider.getMoveUrl(#media,-1)"/>"><img src="/images/left.png" alt="Move left"/></a>
						<a class="img-button" data-toggle="modal" data-target="#myModal" href="<s:property value="mediaProvider.getDeletionUrl(#media)"/>"><img src="/images/delete.png" alt="Delete"/></a>
						<a class="img-button" href="<s:property value="mediaProvider.getMoveUrl(#media,1)"/>"><img src="/images/right.png" alt="Move right"/></a>
					</div>
				</s:if>
		      </div>
		      <s:if test="!#tags.isEmpty()">
				<div class="tags-container">
					<s:set value="25" var="top"/>
					<s:set value="25" var="left"/> 
					<s:iterator value="#tags" var="tag">
						<s:if test="'FACET'.equals(#tag.getDisplayMode())">
							<s:set value="#tagSupport.getTagTranslation(#tag)" var="tagLabel"/>
							<div class="tag-container" style="top: <s:property value="#top"/>px; left: <s:property value="#left"/>px;">
								<img class="search-facet-image" alt="<s:property value="#tagLabel"/>" src="<s:property value="#tagSupport.getTagIconUrl(#tag)"/>">
							</div>
							<s:set value="#top+50" var="top"/>
							<s:if test="#top>=275">
								<s:set value="25" var="top"/>
								<s:set value="#left+50" var="left"/>
							</s:if>
						</s:if>
					</s:iterator>
				</div>
			</s:if>
		    </div>
		    </s:iterator>
		  </div>
		
		  <!-- Controls -->
		  <a class="left carousel-control" href="#photo-carousel" data-slide="prev">
		    <span class="glyphicon glyphicon-chevron-left"></span>
		  </a>
		  <a class="right carousel-control" href="#photo-carousel" data-slide="next">
		    <span class="glyphicon glyphicon-chevron-right"></span>
		  </a>
		</div>
	</s:else>
</div>
