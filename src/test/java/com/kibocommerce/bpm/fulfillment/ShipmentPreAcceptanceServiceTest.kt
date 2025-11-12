package com.kibocommerce.bpm.fulfillment.service

import org.jbpm.test.JbpmJUnitBaseTestCase
import org.junit.Before
import org.junit.Test
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.process.WorkItem
import org.kie.api.runtime.process.WorkItemManager
import org.kie.api.runtime.process.WorkflowProcessInstance
import org.kie.api.task.TaskService
import org.mockito.Mockito.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class ShipmentPreAcceptanceServiceTest : JbpmJUnitBaseTestCase(true, false) {
    
    private var kieSession: KieSession? = null
    private var taskService: TaskService? = null
    private lateinit var service: ShipmentPreAcceptanceService
    
    @Before
    fun init() {
        createRuntimeManager("com/kibocommerce/bpm/fulfillment/TLG_Custom_BOPIS_Process.bpmn")
        val runtimeEngine = getRuntimeEngine(null)
        kieSession = runtimeEngine.kieSession
        taskService = runtimeEngine.taskService
        service = ShipmentPreAcceptanceService()
    }

    @Test
    fun `executeWorkItem with null currentState sets default state`() {
        val workItem = mock(WorkItem::class.java)
        val workItemManager = mock(WorkItemManager::class.java)
        val parameters = mutableMapOf<String, Any>(
            "order" to mapOf("orderId" to "12345")
        )
        
        `when`(workItem.parameters).thenReturn(parameters)
        `when`(workItem.id).thenReturn(1L)
        
        service.executeWorkItem(workItem, workItemManager)
        
        verify(workItemManager).completeWorkItem(1L, parameters)
        assertEquals("ACCEPTED_SHIPMENT", parameters["currentState"])
    }

    @Test
    fun `executeWorkItem with existing currentState preserves state`() {
        val workItem = mock(WorkItem::class.java)
        val workItemManager = mock(WorkItemManager::class.java)
        val parameters = mutableMapOf<String, Any>(
            "order" to mapOf("orderId" to "12345"),
            "currentState" to "EXISTING_STATE"
        )
        
        `when`(workItem.parameters).thenReturn(parameters)
        `when`(workItem.id).thenReturn(1L)
        
        service.executeWorkItem(workItem, workItemManager)
        
        verify(workItemManager).completeWorkItem(1L, parameters)
        assertEquals("EXISTING_STATE", parameters["currentState"])
    }

    @Test
    fun `executeWorkItem with null order still completes`() {
        val workItem = mock(WorkItem::class.java)
        val workItemManager = mock(WorkItemManager::class.java)
        val parameters = mutableMapOf<String, Any?>(
            "order" to null
        )
        
        `when`(workItem.parameters).thenReturn(parameters)
        `when`(workItem.id).thenReturn(1L)
        
        service.executeWorkItem(workItem, workItemManager)
        
        verify(workItemManager).completeWorkItem(1L, parameters)
        assertEquals("ACCEPTED_SHIPMENT", parameters["currentState"])
    }

    @Test
    fun `executeWorkItem with exception logs error`() {
        val workItem = mock(WorkItem::class.java)
        val workItemManager = mock(WorkItemManager::class.java)
        
        `when`(workItem.parameters).thenThrow(RuntimeException("Test exception"))
        `when`(workItem.id).thenReturn(1L)
        
        assertFailsWith<RuntimeException> {
            service.executeWorkItem(workItem, workItemManager)
        }
    }
    
    @Test
    fun `testWithinBPMNProcess`() {
        val process = kieSession?.startProcess(
            "com.kibocommerce.bpm.fulfillment.TLG_Custom_BOPIS_Process",
            mapOf(
                "order" to mapOf("orderId" to "12345"),
                "currentState" to "PENDING"
            )
        ) as? WorkflowProcessInstance
        
        assertNotNull(process)
        assertEquals("ACCEPTED_SHIPMENT", process?.getVariable("currentState"))
    }
}
