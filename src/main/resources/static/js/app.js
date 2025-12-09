/**
 * App.js - Funcionalidades comunes del sistema de inventario
 * @author Sistema de Inventario - Droguería Inti
 */

document.addEventListener('DOMContentLoaded', function() {
    console.log('Sistema de Inventario inicializado');

    // Inicializar tooltips de Bootstrap
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Confirmación para eliminar
    document.querySelectorAll('.btn-delete').forEach(button => {
        button.addEventListener('click', function(e) {
            const confirmMessage = this.getAttribute('data-confirm-message') || 
                '¿Está seguro de que desea eliminar este registro? Esta acción no se puede deshacer.';
            
            if (!confirm(confirmMessage)) {
                e.preventDefault();
            }
        });
    });

    // Auto-ocultar alertas después de 5 segundos
    document.querySelectorAll('.alert.auto-dismiss').forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Validación de formularios
    document.querySelectorAll('form.needs-validation').forEach(form => {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Mostrar/Ocultar contraseña
    document.querySelectorAll('.toggle-password').forEach(button => {
        button.addEventListener('click', function() {
            const input = document.querySelector(this.getAttribute('data-target'));
            const icon = this.querySelector('i');
            
            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                input.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        });
    });

    // Inicializar selects con búsqueda
    if (typeof $.fn.select2 !== 'undefined') {
        $('.select2').select2({
            theme: 'bootstrap-5',
            width: '100%'
        });
    }

    // Inicializar datepickers
    if (typeof flatpickr !== 'undefined') {
        flatpickr('.datepicker', {
            dateFormat: "Y-m-d",
            locale: "es"
        });
        
        flatpickr('.datetimepicker', {
            enableTime: true,
            dateFormat: "Y-m-d H:i",
            locale: "es"
        });
    }
});

// Funciones de utilidad global
const app = {
    /**
     * Formatear número como moneda
     * @param {number} amount - Cantidad a formatear
     * @returns {string} - Cantidad formateada como moneda
     */
    formatCurrency: function(amount) {
        return new Intl.NumberFormat('es-PE', {
            style: 'currency',
            currency: 'PEN'
        }).format(amount);
    },
    
    /**
     * Formatear fecha
     * @param {string|Date} date - Fecha a formatear
     * @returns {string} - Fecha formateada
     */
    formatDate: function(date) {
        if (!date) return '-';
        
        const d = new Date(date);
        return d.toLocaleDateString('es-PE', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        });
    },
    
    /**
     * Formatear fecha y hora
     * @param {string|Date} dateTime - Fecha y hora a formatear
     * @returns {string} - Fecha y hora formateadas
     */
    formatDateTime: function(dateTime) {
        if (!dateTime) return '-';
        
        const d = new Date(dateTime);
        return d.toLocaleString('es-PE', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    },
    
    /**
     * Mostrar loading en botones
     * @param {HTMLElement} button - Botón en el que mostrar el loading
     * @returns {function} - Función para restaurar el estado original del botón
     */
    showLoading: function(button) {
        const originalText = button.innerHTML;
        button.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Procesando...';
        button.disabled = true;
        
        return function() {
            button.innerHTML = originalText;
            button.disabled = false;
        };
    }
};

// Exportar app para uso global
window.app = app;