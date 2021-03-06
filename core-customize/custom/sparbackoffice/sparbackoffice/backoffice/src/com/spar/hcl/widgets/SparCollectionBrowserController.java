/**
 *
 */
package com.spar.hcl.widgets;

import com.spar.hcl.constants.SparbackofficeConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Popup;
import org.zkoss.zul.event.PagingEvent;

import com.google.common.collect.Maps;
import com.hybris.cockpitng.annotations.GlobalCockpitEvent;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.components.Actions;
import com.hybris.cockpitng.components.DefaultCockpitActionsRenderer;
import com.hybris.cockpitng.core.async.Operation;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListView;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.core.model.ModelObserver;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.search.data.pageable.Pageable;
import com.hybris.cockpitng.search.data.pageable.PageableList;
import com.hybris.cockpitng.type.ObjectValueService;
import com.hybris.cockpitng.util.BackofficeSpringUtil;
import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.widgets.collectionbrowser.CollectionBrowserController;
import com.hybris.cockpitng.widgets.collectionbrowser.mold.CollectionBrowserMoldContext;
import com.hybris.cockpitng.widgets.collectionbrowser.mold.CollectionBrowserMoldStrategy;
import com.hybris.cockpitng.widgets.collectionbrowser.mold.impl.SinglePage;
import com.hybris.cockpitng.widgets.navigation.NavigationItemSelectorContext;


/**
 * This class is used to create a controller by extending CollectionBrowserController. This is used to Export Custom CSV
 * generation widget.
 *
 * @author rohan_c
 *
 */
public class SparCollectionBrowserController extends CollectionBrowserController
{
	private static final Logger LOG = LoggerFactory.getLogger(CollectionBrowserController.class);
	private final Object monitor;
	private TypeFacade typeFacade;

	//Custom List Action
	private Actions actions;

	@Wire
	private Label listTitle;

	@Wire
	private Label listSubtitle;

	@Wire
	private Button itemCount;

	@Wire
	private Button deselectAll;

	@Wire
	private Popup deselectPopup;

	@Wire
	private Paging paging;

	@Wire
	private Div statusBar;

	@Wire
	private Div browserContainer;

	@Wire
	private Hbox moldSelectorContainer;

	@Wire
	private Actions actionSlot;
	private LabelService labelService;
	private ObjectValueService objectValueService;
	private CockpitLocaleService cockpitLocaleService;
	private PermissionFacade permissionFacade;
	private List<CollectionBrowserMoldStrategy> availableMolds;
	private CollectionBrowserMoldStrategy activeMold;

	/**
	 * Constructor
	 */
	public SparCollectionBrowserController()
	{
		this.monitor = new Object();
	}


	//@Override
	@SocketEvent(socketId = "pageable")
	public void setPageable(final Pageable<?> pageable)
	{
		setValue(SparbackofficeConstants.SELECTED_OBJECTS, Collections.emptySet());
		if (pageable != null)
		{
			if (pageable.getPageSize() != getWidgetSettings().getInt(SparbackofficeConstants.PAGE_SIZE))
			{
				getWidgetSettings().put(SparbackofficeConstants.PAGE_SIZE, Integer.valueOf(pageable.getPageSize()));
			}
			final String typeCode = pageable.getTypeCode();
			initializeDataType(typeCode);
			initializeActionSlot(typeCode);
			updateTitle(pageable);
		}
		else
		{
			updateTitle(null);
		}
		getActiveMold().setPage((SinglePage) pageable);
	}

	/**
	 * This method is used to update the title of the collection Browser
	 *
	 * @param pageable
	 */
	protected void updateTitle(final Pageable<?> pageable)
	{
		if (this.getListTitle() == null)
		{
			return;
		}
		final String title = getLabel(getWidgetSettings().getString(SparbackofficeConstants.GET_LIST_TITLE));
		this.getListTitle().setValue(StringUtils.defaultIfBlank(title, ""));

		final String subTitle = getLabel(getWidgetSettings().getString(SparbackofficeConstants.GET_LIST_SUB_TITLE));
		this.getListSubtitle().setValue(
				(StringUtils.isBlank(subTitle)) ? getWidgetSettings().getString(SparbackofficeConstants.GET_LIST_SUB_TITLE)
						: subTitle);

		if (pageable != null)
		{
			final StringBuilder subTitleBuilder = new StringBuilder();
			final int totalCount = pageable.getTotalCount();
			if (totalCount >= 0)
			{
				subTitleBuilder.append(totalCount);
			}
			else
			{
				subTitleBuilder.append(0);
			}

			if (StringUtils.isNotBlank(pageable.getTypeCode()))
			{
				String objectLabel = getLabelService().getObjectLabel(pageable.getTypeCode());
				if (StringUtils.isBlank(objectLabel))
				{
					objectLabel = Labels.getLabel(String.format("%s%s", new Object[]
					{ pageable.getTypeCode(), "-name" }));
				}
				subTitleBuilder.append(" (").append(objectLabel).append(')');
			}

			this.getListSubtitle().setValue(subTitleBuilder.toString());
		}

		suppressTitleVisibilityForCollapsibleContainer();
	}

	/**
	 * This method is used to suppress the collapsible container title
	 */
	protected void suppressTitleVisibilityForCollapsibleContainer()
	{
		if ((getWidgetslot() == null)
				|| (getWidgetslot().getParentWidgetInstance() == null)
				|| (getWidgetslot().getParentWidgetInstance().getWidget() == null)
				|| (!("com.hybris.cockpitng.collapsiblecontainer".equals(getWidgetslot().getParentWidgetInstance().getWidget()
						.getWidgetDefinitionId()))))
		{
			return;
		}
		this.getListTitle().setVisible(false);
		this.getListSubtitle().setVisible(false);
		setWidgetTitle(new StringBuilder().append(this.getListTitle().getValue()).append(" ")
				.append(this.getListSubtitle().getValue()).toString());
	}


	@Override
	@SocketEvent(socketId = "reset")
	public void reset(final Map<Object, Object> params)
	{
		resetModel();
		resetView();

		for (final CollectionBrowserMoldStrategy mold : this.availableMolds)
		{
			mold.reset();
		}
		this.activeMold.setPage(null);
	}


	@Override
	@SocketEvent(socketId = "previousItemSelectorInvocation")
	public void previousItemSelectorInvocation()
	{
		getActiveMold().previousItemSelectorInvocation();
	}

	@Override
	@SocketEvent(socketId = "nextItemSelectorInvocation")
	public void nextItemSelectorInvocation()
	{
		getActiveMold().nextItemSelectorInvocation();
	}

	/**
	 * This method is used to initialize ActionSlot for Custom CSV Export
	 *
	 * @param typeCode
	 */
	protected void initializeActionSlot(final String typeCode)
	{
		if (StringUtils.isNotBlank(typeCode))
		{
			final String componentCtxt = getWidgetSettings().getString(SparbackofficeConstants.ACTION_SLOT_COMPONENET_ID);
			getActionSlot().setRenderer(DefaultCockpitActionsRenderer.class.getName());
			getActionSlot().setConfig(String.format(SparbackofficeConstants.ACTION_CONF, new Object[]
			{ componentCtxt, typeCode }));
		}
		else
		{
			getActionSlot().setConfig(null);
		}
		getActionSlot().reload();
	}

	/**
	 * This method is used to initialize the Data Type for the Collection Browser
	 *
	 * @param typeCode
	 */
	public void initializeDataType(final String typeCode)
	{
		DataType dataType = null;
		try
		{
			dataType = this.typeFacade.load(typeCode);
		}
		catch (final TypeNotFoundException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(new StringBuilder().append("Type '").append(typeCode).append("' could not be loaded").toString(),
						e.getMessage());
			}
		}
		setValue(SparbackofficeConstants.DATA_TYPE, dataType);
		setValue(SparbackofficeConstants.DATA_TYPE_CODE, typeCode);
	}


	@Override
	public void initialize(final Component comp)
	{
		if (CollectionUtils.isEmpty(this.availableMolds))
		{
			loadAvailableMolds();
		}
		initializeMoldSelector();
		getModel().addObserver(SparbackofficeConstants.PAGEABLE, new ModelObserver()
		{
			@Override
			public void modelChanged()
			{
				final Pageable pageable = SparCollectionBrowserController.this.getValue(SparbackofficeConstants.PAGEABLE,
						Pageable.class);
				final ListView listView = SparCollectionBrowserController.this.getValue("columnConfig", ListView.class);
				final HashMap map = Maps.newHashMap();
				map.put(SparbackofficeConstants.PAGEABLE, pageable);
				map.put("columnConfig", listView);
				SparCollectionBrowserController.this.setValue("exportColumnsAndData", map);
				changeCustomCSVExportListAction(pageable);
				SparCollectionBrowserController.this.sendNavigationItemSelectorContext(SparCollectionBrowserController.this
						.getActiveMold().getNavigationItemSelectorContext());
			}

			/**
			 * This method is used to make the Custom CSV Export List icon Visible
			 *
			 * @param pageable
			 */
			protected void changeCustomCSVExportListAction(final Pageable pageable)
			{
				if (null != pageable && SparbackofficeConstants.CONSIGNMENT_TYPE_CODE.equals(pageable.getTypeCode())
						&& null != actions)
				{
					actions.setVisible(true);
					actions.setFocus(true);
				}
				else
				{
					actions.setVisible(false);
					actions.setFocus(false);
				}
			}
		});
		if (getValue(SparbackofficeConstants.SELECTED_OBJECTS, Set.class) == null)
		{
			setValue(SparbackofficeConstants.SELECTED_OBJECTS, Collections.emptySet());
		}
		getModel().addObserver(SparbackofficeConstants.SELECTED_OBJECTS, new ModelObserver()
		{
			@Override
			public void modelChanged()
			{
				SparCollectionBrowserController.this.sendNavigationItemSelectorContext(SparCollectionBrowserController.this
						.getActiveMold().getNavigationItemSelectorContext());
			}
		});
		final Pageable pageable = getValue(SparbackofficeConstants.PAGEABLE, Pageable.class);
		if (pageable != null)
		{
			final String typeCode = pageable.getTypeCode();
			initializeDataType(typeCode);
			initializeActionSlot(typeCode);
		}
		this.paging.addEventListener("onPaging", new EventListener()
		{
			public void onEvent(final PagingEvent event)
			{
				//((Object) SparCollectionBrowserController.this.getActiveMold()).onPagingEvent(event);
				SparCollectionBrowserController.this.sendNavigationItemSelectorContext(new NavigationItemSelectorContext(
						SparCollectionBrowserController.this.getPageableCurrentPageSize(), -1));
			}

			@Override
			public void onEvent(final Event event) throws Exception
			{
				onEvent((PagingEvent) event);

			}
		});
		this.itemCount.setDisabled(true);
		updateTitle(pageable);
		initializeActionSlot(getValue(SparbackofficeConstants.DATA_TYPE_CODE, String.class));
		getActiveMold().render(this.browserContainer, null);
	}

	/**
	 * This method is used to load available molds
	 */
	public void loadAvailableMolds()
	{
		this.availableMolds = new ArrayList();

		final Map<String, CollectionBrowserMoldStrategy> beans = BackofficeSpringUtil
				.getAllBeans(CollectionBrowserMoldStrategy.class);

		for (final CollectionBrowserMoldStrategy bean : beans.values())
		{
			bean.setContext((CollectionBrowserMoldContext) this);
			this.availableMolds.add(bean);
		}
		Collections.sort(this.availableMolds, AnnotationAwareOrderComparator.INSTANCE);
	}

	/**
	 * This method is used to initialize the mold selector and set the classes
	 */
	public void initializeMoldSelector()
	{
		this.moldSelectorContainer.getChildren().clear();
		final CollectionBrowserMoldStrategy effectiveMold = getActiveMold();
		for (final CollectionBrowserMoldStrategy mold : this.availableMolds)
		{
			final String moldName = StringUtils.defaultIfBlank(mold.getName(), "unknown").toLowerCase();
			final Div moldSelector = new Div();
			moldSelector.setAttribute("moldClass", mold);
			moldSelector.setSclass("yw-coll-browser-mold-sel-btn");
			if (mold.equals(effectiveMold))
			{
				UITools.modifySClass(moldSelector, String.format(SCLASS_ACTIVE_MOLD_SELECTOR, new Object[]
				{ moldName }), true);
				UITools.modifySClass(moldSelector, String.format(SCLASS_INACTIVE_MOLD_SELECTOR, new Object[]
				{ moldName }), false);
			}
			else
			{
				UITools.modifySClass(moldSelector, String.format(SCLASS_ACTIVE_MOLD_SELECTOR, new Object[]
				{ moldName }), false);
				UITools.modifySClass(moldSelector, String.format(SCLASS_INACTIVE_MOLD_SELECTOR, new Object[]
				{ moldName }), true);
			}
			moldSelector.addEventListener("onClick", new EventListener()
			{
				@Override
				public void onEvent(final Event event)
				{
					synchronized (SparCollectionBrowserController.this.monitor)
					{
						if (ObjectUtils.notEqual(SparCollectionBrowserController.this.getActiveMold(), getActiveMold()))
						{
							for (final Component component : SparCollectionBrowserController.this.moldSelectorContainer.getChildren())
							{
								final CollectionBrowserMoldStrategy moldClass = (CollectionBrowserMoldStrategy) component
										.getAttribute("moldClass");

								if (ObjectUtils.notEqual(moldClass, getActiveMold()))
								{
									UITools.modifySClass((HtmlBasedComponent) component,
											String.format(SCLASS_ACTIVE_MOLD_SELECTOR, new Object[]
											{ moldClass.getName() }), false);
									UITools.modifySClass((HtmlBasedComponent) component,
											String.format(SCLASS_INACTIVE_MOLD_SELECTOR, new Object[]
											{ moldClass.getName() }), true);
									moldClass.release();
								}
								else
								{
									UITools.modifySClass(actionSlot, String.format(SCLASS_ACTIVE_MOLD_SELECTOR, new Object[]
									{ moldClass.getName() }), true);

									UITools.modifySClass(actionSlot, String.format(SCLASS_INACTIVE_MOLD_SELECTOR, new Object[]
									{ moldClass.getName() }), false);

									UITools.modifySClass(actions, String.format(SCLASS_ACTIVE_MOLD_SELECTOR, new Object[]
									{ moldClass.getName() }), true);

									UITools.modifySClass(actions, String.format(SCLASS_INACTIVE_MOLD_SELECTOR, new Object[]
									{ moldClass.getName() }), false);

									SparCollectionBrowserController.this.setActiveMold(getActiveMold());
									getActiveMold().render(SparCollectionBrowserController.this.browserContainer, null);
									getActiveMold().setPage(
											(SinglePage) SparCollectionBrowserController.this.getValue(SparbackofficeConstants.PAGEABLE, Pageable.class));
								}
							}
						}
					}
				}
			});
			moldSelector.setTooltiptext(mold.getTooltipText());
			this.moldSelectorContainer.appendChild(moldSelector);
		}
	}

	//@Override
	public void buildPaging(final Pageable<?> pageable)
	{
		if ((pageable != null) && (((pageable.hasNextPage()) || (pageable.hasPreviousPage()))))
		{
			notifyPaging(pageable);
		}
		else
		{
			this.paging.setVisible(false);
			this.browserContainer.setVflex("1");
			Clients.resize(this.browserContainer);
		}
	}

	/**
	 * This method is used to notify the widget if paging is enabled.
	 *
	 * @param pageableData
	 */
	protected void notifyPaging(final Pageable<?> pageableData)
	{

		this.paging.setVisible(true);
		final int pageSize = pageableData.getPageSize();
		this.paging.setPageSize((pageSize > 0) ? pageSize : getWidgetSettings().getInt(SparbackofficeConstants.PAGE_SIZE));

		final int pageNumber = pageableData.getPageNumber();
		final int totalCount = pageableData.getTotalCount();

		this.paging.setTotalSize(totalCount);

		if (totalCount < pageNumber)
		{
			this.paging.setActivePage(0);
			pageableData.setPageNumber(0);
			refreshBrowser(pageableData);
		}
		else
		{
			this.paging.setActivePage(pageNumber);
		}

		this.browserContainer.setVflex("1");
		Clients.resize(this.browserContainer);
	}

	//@Override
	protected void refreshBrowser(final Pageable<?> pageable)
	{
		if (pageable == null)
		{
			return;
		}
		pageable.refresh();
		setPageable(pageable);
	}


	@Override
	@SocketEvent(socketId = "list")
	public void setList(final List<Object> objects)
	{
		if (CollectionUtils.isNotEmpty(objects))
		{
			final String typeCode = resolveTypeCodeFromCollection(objects);
			setPageable(new PageableList(objects, getWidgetSettings().getInt(SparbackofficeConstants.PAGE_SIZE), typeCode));
		}
		else
		{
			setPageable(null);
		}
	}

	/**
	 * This method is used to reset the Models
	 */
	protected void resetModel()
	{
		setValue(SparbackofficeConstants.PAGEABLE, null);
		setValue(SparbackofficeConstants.DATA_TYPE, null);
		setValue(SparbackofficeConstants.DATA_TYPE_CODE, null);
		setValue(SparbackofficeConstants.SELECTED_OBJECTS, null);
		setValue(SparbackofficeConstants.ACTIVE_MODEL_NAME, null);
	}

	/**
	 * This method is used to reset the view
	 */
	protected void resetView()
	{
		updateTitle(null);
		initializeActionSlot(null);
		this.paging.setVisible(false);
	}

	@Override
	@GlobalCockpitEvent(eventName = "objectDeleted", scope = "session")
	public void handleObjectDeleteEvent(final CockpitEvent event)
	{
		final Pageable pageable = getValue(SparbackofficeConstants.PAGEABLE, Pageable.class);
		String typeCode;
		if (event.getData() instanceof Collection)
		{
			typeCode = resolveTypeCodeFromCollection((Collection) event.getData());
		}
		else
		{
			typeCode = this.typeFacade.getType(event.getData());
		}

		if ((pageable == null) || (!(StringUtils.isNotBlank(pageable.getTypeCode()))) || (!(StringUtils.isNotBlank(typeCode)))
				|| (!(getActiveMold().isHandlingObjectEvents(typeCode))))
		{
			return;
		}
		refreshBrowser(pageable);
		getActiveMold().handleObjectDeleteEvent(event);
	}

	//@Override
	@GlobalCockpitEvent(eventName = "objectUpdated", scope = "session")
	public void handleObjectUpdateEvent(final CockpitEvent event)
	{
		final Pageable pageable = getValue(SparbackofficeConstants.PAGEABLE, Pageable.class);
		final String typeCode = this.typeFacade.getType(event.getData());
		final Object data = event.getData();
		if ((data == null) || (pageable == null) || (!(StringUtils.isNotBlank(typeCode)))
				|| (!(StringUtils.isNotBlank(pageable.getTypeCode())))
				|| (!(getActiveMold().isHandlingObjectEvents(typeCode))))
		{
			return;
		}
		getActiveMold().handleObjectUpdateEvent(event);
	}

	@Override
	@GlobalCockpitEvent(eventName = "objectCreated", scope = "session")
	public void handleObjectCreateEvent(final CockpitEvent event)
	{
		getActiveMold().handleObjectCreateEvent(event);
	}

	@Override
	protected String resolveTypeCodeFromCollection(final Collection<?> objects)
	{
		String typeCode = null;
		if (CollectionUtils.isNotEmpty(objects))
		{
			final Object firstItem = objects.iterator().next();
			if (firstItem != null)
			{
				typeCode = this.typeFacade.getType(firstItem);
				if (typeCode == null)
				{
					typeCode = firstItem.getClass().getName();
				}
			}
		}
		return typeCode;
	}

	//@Override
	public void updateItemCount(final Collection<?> selection)
	{
		final String currentItemCount = String.valueOf((selection == null) ? 0 : selection.size());

		this.itemCount.setLabel(getLabel("itemCount", new Object[]
		{ currentItemCount }));
		this.itemCount.setDisabled(CollectionUtils.isEmpty(selection));
	}

	@Override
	@ViewEvent(componentID = "itemCount", eventName = "onClick")
	public void showPopup()
	{
		UITools.modifySClass(this.itemCount, "yw-listview-statusbar-button-active", true);
		this.deselectPopup.open(this.statusBar, "before_start");
		this.deselectAll.focus();
	}

	@Override
	@ViewEvent(componentID = "deselectAll", eventName = "onClick")
	public void deselectItems()
	{
		setValue(SparbackofficeConstants.SELECTED_OBJECTS, Collections.emptyList());
		final Set selection = Collections.emptySet();
		getActiveMold().selectItems(selection);
		updateItemCount(selection);
		sendOutput("selectedItems", selection);
		sendOutput("selectedItem", null);
		this.deselectPopup.close();
		removeActiveSclass();
	}

	//@Override
	@ViewEvent(componentID = "deselectAll", eventName = "onBlur")
	public void removeActiveSclass()
	{
		UITools.modifySClass(this.itemCount, "yw-listview-statusbar-button-active", false);
	}

	@Override
	public void setStatusBarVisible(final boolean visible)
	{
		this.statusBar.setVisible(visible);
	}

	@Override
	public void callExecuteOperation(final Operation operation, final EventListener<Event> callbackEvent, final String busyMessage)
	{
		executeOperation(operation, callbackEvent, busyMessage);
	}

	@Override
	public CollectionBrowserMoldStrategy getActiveMold()
	{
		if (this.activeMold == null)
		{
			final String activeMoldName = getModel().getValue("activeMoldName", String.class);
			if (StringUtils.isNotBlank(activeMoldName))
			{
				for (final CollectionBrowserMoldStrategy mold : this.availableMolds)
				{
					if (StringUtils.equals(mold.getName(), activeMoldName))
					{
						this.activeMold = mold;
						return this.activeMold;
					}
				}
			}
			setActiveMold(this.availableMolds.iterator().next());
		}
		return this.activeMold;
	}

	@Override
	public void setActiveMold(final CollectionBrowserMoldStrategy activeMold)
	{
		this.activeMold = activeMold;
		getModel().put("activeMoldName", activeMold.getName());
	}

	//@Override
	public int getPageableCurrentPageSize()
	{
		final Pageable pageable = getValue(SparbackofficeConstants.PAGEABLE, Pageable.class);
		if ((pageable != null) && (pageable.getCurrentPage() != null))
		{
			return pageable.getCurrentPage().size();
		}

		return 0;
	}

	@Override
	public void sendNavigationItemSelectorContext(final NavigationItemSelectorContext navigationContext)
	{
		sendOutput("previousItemSelectorContext", navigationContext);
		sendOutput("nextItemSelectorContext", navigationContext);
	}

	@Override
	public LabelService getLabelService()
	{
		return this.labelService;
	}

	@Override
	@Required
	public void setLabelService(final LabelService labelService)
	{
		this.labelService = labelService;
	}

	@Override
	public TypeFacade getTypeFacade()
	{
		return this.typeFacade;
	}

	@Override
	@Required
	public void setTypeFacade(final TypeFacade typeFacade)
	{
		this.typeFacade = typeFacade;
	}

	@Override
	public ObjectValueService getObjectValueService()
	{
		return this.objectValueService;
	}

	@Override
	@Required
	public void setObjectValueService(final ObjectValueService objectValueService)
	{
		this.objectValueService = objectValueService;
	}

	@Override
	public CockpitLocaleService getCockpitLocaleService()
	{
		return this.cockpitLocaleService;
	}

	@Override
	@Required
	public void setCockpitLocaleService(final CockpitLocaleService cockpitLocaleService)
	{
		this.cockpitLocaleService = cockpitLocaleService;
	}

	@Override
	public PermissionFacade getPermissionFacade()
	{
		return this.permissionFacade;
	}

	@Override
	@Required
	public void setPermissionFacade(final PermissionFacade permissionFacade)
	{
		this.permissionFacade = permissionFacade;
	}

	@Override
	public Label getListTitle()
	{
		return this.listTitle;
	}

	@Override
	public Label getListSubtitle()
	{
		return this.listSubtitle;
	}

	@Override
	public Button getItemCount()
	{
		return this.itemCount;
	}

	@Override
	public Button getDeselectAll()
	{
		return this.deselectAll;
	}

	@Override
	public Popup getDeselectPopup()
	{
		return this.deselectPopup;
	}

	@Override
	public Paging getPaging()
	{
		return this.paging;
	}

	@Override
	public Div getStatusBar()
	{
		return this.statusBar;
	}

	@Override
	public Div getBrowserContainer()
	{
		return this.browserContainer;
	}

	@Override
	public Hbox getMoldSelectorContainer()
	{
		return this.moldSelectorContainer;
	}

	@Override
	public Actions getActionSlot()
	{
		return this.actionSlot;
	}

	@Override
	public void setAvailableMolds(final List<CollectionBrowserMoldStrategy> availableMolds)
	{
		this.availableMolds = availableMolds;
	}


	/**
	 * Getter
	 *
	 * @return the actions
	 */
	public Actions getActions()
	{
		return actions;
	}


	/**
	 * Setter
	 *
	 * @param actions
	 *           the actions to set
	 */
	public void setActions(final Actions actions)
	{
		this.actions = actions;
	}


	/**
	 * Getter
	 *
	 * @return the monitor
	 */
	public Object getMonitor()
	{
		return monitor;
	}


	/**
	 * Getter
	 *
	 * @return the availableMolds
	 */
	public List<CollectionBrowserMoldStrategy> getAvailableMolds()
	{
		return availableMolds;
	}


	/**
	 * Setter
	 *
	 * @param listTitle
	 *           the listTitle to set
	 */
	public void setListTitle(final Label listTitle)
	{
		this.listTitle = listTitle;
	}


	/**
	 * Setter
	 *
	 * @param listSubtitle
	 *           the listSubtitle to set
	 */
	public void setListSubtitle(final Label listSubtitle)
	{
		this.listSubtitle = listSubtitle;
	}


	/**
	 * Setter
	 *
	 * @param itemCount
	 *           the itemCount to set
	 */
	public void setItemCount(final Button itemCount)
	{
		this.itemCount = itemCount;
	}


	/**
	 * Setter
	 *
	 * @param deselectAll
	 *           the deselectAll to set
	 */
	public void setDeselectAll(final Button deselectAll)
	{
		this.deselectAll = deselectAll;
	}


	/**
	 * Setter
	 *
	 * @param deselectPopup
	 *           the deselectPopup to set
	 */
	public void setDeselectPopup(final Popup deselectPopup)
	{
		this.deselectPopup = deselectPopup;
	}


	/**
	 * Setter
	 *
	 * @param paging
	 *           the paging to set
	 */
	public void setPaging(final Paging paging)
	{
		this.paging = paging;
	}


	/**
	 * Setter
	 *
	 * @param statusBar
	 *           the statusBar to set
	 */
	public void setStatusBar(final Div statusBar)
	{
		this.statusBar = statusBar;
	}


	/**
	 * Setter
	 *
	 * @param browserContainer
	 *           the browserContainer to set
	 */
	public void setBrowserContainer(final Div browserContainer)
	{
		this.browserContainer = browserContainer;
	}


	/**
	 * Setter
	 *
	 * @param moldSelectorContainer
	 *           the moldSelectorContainer to set
	 */
	public void setMoldSelectorContainer(final Hbox moldSelectorContainer)
	{
		this.moldSelectorContainer = moldSelectorContainer;
	}


	/**
	 * Setter
	 *
	 * @param actionSlot
	 *           the actionSlot to set
	 */
	public void setActionSlot(final Actions actionSlot)
	{
		this.actionSlot = actionSlot;
	}


}
