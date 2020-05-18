<div id="savedaddressbook" class="modal fade" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title text-left">Please select from following delivery addresses</h4>
			</div>
			<div class="modal-body">
				<div class="savedAddress">
					<div class="page-loader">
						<img id="location-loader" src="${commonResourcePath}/images/loader_blue.gif">
					</div>					
					<div class="addressViewMsg hide">Session is expired. Please <span id="userLoginSession">login</span> again to view exiting addresses.</div>
					<div class="no-address-msg hide">No Delivery Addresses Available.</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" id="chkSavedAddress" class="btn btn-primary">Submit</button>	
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>															
			</div>
		</div>
	</div>
</div> 