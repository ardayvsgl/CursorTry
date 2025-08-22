// Student Management Application JavaScript
class StudentApp {
    constructor() {
        this.apiBaseUrl = '/api/students';
        this.currentStudents = [];
        this.studentModal = null;
        this.deleteModal = null;
        this.toast = null;
        this.init();
    }

    init() {
        this.studentModal = new bootstrap.Modal(document.getElementById('studentModal'));
        this.deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
        

        this.successToast = new bootstrap.Toast(document.getElementById('successToast'));
        this.errorToast = new bootstrap.Toast(document.getElementById('errorToast'));
        

        this.loadStudents();
        

        this.addEventListeners();
    }

    addEventListeners() {
        // Search input enter key
        document.getElementById('searchInput').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.searchStudents();
            }
        });

        // Age filter enter key
        document.getElementById('minAge').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.filterByAge();
            }
        });
        document.getElementById('maxAge').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.filterByAge();
            }
        });
    }

    // API Methods
    async loadStudents() {
        try {
            this.showLoading(true);
            const response = await fetch(this.apiBaseUrl);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const students = await response.json();
            this.currentStudents = students;
            this.displayStudents(students);
            this.updateStatistics(students);
            this.hideNoResults();
        } catch (error) {
            console.error('Error loading students:', error);
            this.showError('Öğrenciler yüklenirken hata oluştu: ' + error.message);
        } finally {
            this.showLoading(false);
        }
    }

    async createStudent(studentData) {
        try {
            const response = await fetch(this.apiBaseUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(studentData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Öğrenci eklenirken hata oluştu');
            }

            const newStudent = await response.json();
            this.currentStudents.push(newStudent);
            this.displayStudents(this.currentStudents);
            this.updateStatistics(this.currentStudents);
            this.showSuccess('Öğrenci başarıyla eklendi!');
            this.studentModal.hide();
            this.clearForm();
            return newStudent;
        } catch (error) {
            console.error('Error creating student:', error);
            this.showError(error.message);
            throw error;
        }
    }

    async updateStudent(id, studentData) {
        try {
            const response = await fetch(`${this.apiBaseUrl}/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(studentData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Öğrenci güncellenirken hata oluştu');
            }

            const updatedStudent = await response.json();
            const index = this.currentStudents.findIndex(s => s.id === id);
            if (index !== -1) {
                this.currentStudents[index] = updatedStudent;
            }
            
            this.displayStudents(this.currentStudents);
            this.updateStatistics(this.currentStudents);
            this.showSuccess('Öğrenci başarıyla güncellendi!');
            this.studentModal.hide();
            this.clearForm();
            return updatedStudent;
        } catch (error) {
            console.error('Error updating student:', error);
            this.showError(error.message);
            throw error;
        }
    }

    async deleteStudent(id) {
        console.log('deleteStudent method called with id:', id);
        try {
            console.log('Sending DELETE request to:', `${this.apiBaseUrl}/${id}`);
            
            const response = await fetch(`${this.apiBaseUrl}/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            console.log('DELETE response status:', response.status);
            console.log('DELETE response ok:', response.ok);

            if (!response.ok) {
                let errorMessage = 'Öğrenci silinirken hata oluştu';
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorMessage;
                } catch (e) {
                    console.log('Could not parse error response');
                }
                throw new Error(errorMessage);
            }

            console.log('Student deleted successfully, updating UI...');
            
            // Remove from current students array
            this.currentStudents = this.currentStudents.filter(s => s.id !== id);
            console.log('Updated students array:', this.currentStudents);
            
            // Update UI
            this.displayStudents(this.currentStudents);
            this.updateStatistics(this.currentStudents);
            
            // Show success message
            this.showSuccess('Öğrenci başarıyla silindi!');
            
            // Hide modal
            if (this.deleteModal) {
                this.deleteModal.hide();
                console.log('Delete modal hidden');
            } else {
                console.log('Delete modal not found, trying to hide manually');
                const modalElement = document.getElementById('deleteModal');
                if (modalElement) {
                    const bsModal = bootstrap.Modal.getInstance(modalElement);
                    if (bsModal) {
                        bsModal.hide();
                    }
                }
            }
            
        } catch (error) {
            console.error('Error in deleteStudent:', error);
            this.showError('Silme işlemi başarısız: ' + error.message);
            throw error;
        }
    }

    async searchStudents(query) {
        try {
            this.showLoading(true);
            const response = await fetch(`${this.apiBaseUrl}/search?name=${encodeURIComponent(query)}`);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const students = await response.json();
            this.displayStudents(students);
            this.updateSearchResults(students.length);
            this.hideNoResults();
            
            if (students.length === 0) {
                this.showNoResults();
            }
        } catch (error) {
            console.error('Error searching students:', error);
            this.showError('Arama yapılırken hata oluştu: ' + error.message);
        } finally {
            this.showLoading(false);
        }
    }

    async filterByAge() {
        const minAge = document.getElementById('minAge').value;
        const maxAge = document.getElementById('maxAge').value;
        
        if (!minAge && !maxAge) {
            this.loadStudents();
            return;
        }

        try {
            this.showLoading(true);
            let url = `${this.apiBaseUrl}/age-range?`;
            if (minAge) url += `minAge=${minAge}&`;
            if (maxAge) url += `maxAge=${maxAge}`;
            
            const response = await fetch(url);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const students = await response.json();
            this.displayStudents(students);
            this.updateFilteredResults(students.length);
            this.hideNoResults();
            
            if (students.length === 0) {
                this.showNoResults();
            }
        } catch (error) {
            console.error('Error filtering students:', error);
            this.showError('Filtreleme yapılırken hata oluştu: ' + error.message);
        } finally {
            this.showLoading(false);
        }
    }

    // UI Methods
    displayStudents(students) {
        const tbody = document.getElementById('studentsTableBody');
        tbody.innerHTML = '';

        if (students.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center text-muted">
                        <i class="bi bi-inbox display-4"></i>
                        <p class="mt-2">Henüz öğrenci bulunmuyor</p>
                    </td>
                </tr>
            `;
            return;
        }

        students.forEach(student => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td><span class="badge bg-primary">${student.id}</span></td>
                <td><strong>${this.escapeHtml(student.name)}</strong></td>
                <td><a href="mailto:${student.email}" class="text-decoration-none">${this.escapeHtml(student.email)}</a></td>
                <td><span class="badge bg-info">${student.age}</span></td>
                <td>${this.escapeHtml(student.address || '-')}</td>
                <td>${this.formatDate(student.createdAt)}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary btn-action" onclick="editStudent(${student.id})" title="Düzenle">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger btn-action" onclick="deleteStudent(${student.id}, '${this.escapeHtml(student.name)}')" title="Sil">
                        <i class="bi bi-trash"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-info btn-action" onclick="viewStudent(${student.id})" title="Görüntüle">
                        <i class="bi bi-eye"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(row);
        });
    }

    updateStatistics(students) {
        document.getElementById('totalStudents').textContent = students.length;
        
        if (students.length > 0) {
            const totalAge = students.reduce((sum, student) => sum + student.age, 0);
            const avgAge = Math.round(totalAge / students.length);
            document.getElementById('avgAge').textContent = avgAge;
        } else {
            document.getElementById('avgAge').textContent = '0';
        }
    }

    updateSearchResults(count) {
        document.getElementById('searchResults').textContent = count;
    }

    updateFilteredResults(count) {
        document.getElementById('filteredResults').textContent = count;
    }

    // Form Methods
    showAddForm() {
        document.getElementById('modalTitle').textContent = 'Yeni Öğrenci Ekle';
        document.getElementById('studentId').value = '';
        this.clearForm();
        this.studentModal.show();
    }

    editStudent(id) {
        const student = this.currentStudents.find(s => s.id === id);
        if (!student) {
            this.showError('Öğrenci bulunamadı!');
            return;
        }

        document.getElementById('modalTitle').textContent = 'Öğrenci Düzenle';
        document.getElementById('studentId').value = student.id;
        document.getElementById('name').value = student.name;
        document.getElementById('email').value = student.email;
        document.getElementById('age').value = student.age;
        document.getElementById('address').value = student.address || '';
        
        this.studentModal.show();
    }

    viewStudent(id) {
        const student = this.currentStudents.find(s => s.id === id);
        if (!student) {
            this.showError('Öğrenci bulunamadı!');
            return;
        }

        // Show student details in a simple alert (you can enhance this)
        const details = `
Öğrenci Detayları:
ID: ${student.id}
Ad Soyad: ${student.name}
Email: ${student.email}
Yaş: ${student.age}
Adres: ${student.address || 'Belirtilmemiş'}
Kayıt Tarihi: ${this.formatDate(student.createdAt)}
Güncelleme Tarihi: ${this.formatDate(student.updatedAt)}
        `;
        
        alert(details);
    }

    async saveStudent() {
        const form = document.getElementById('studentForm');
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        const studentData = {
            name: document.getElementById('name').value.trim(),
            email: document.getElementById('email').value.trim(),
            age: parseInt(document.getElementById('age').value),
            address: document.getElementById('address').value.trim()
        };

        try {
            const studentId = document.getElementById('studentId').value;
            
            if (studentId) {
                // Update existing student
                await this.updateStudent(parseInt(studentId), studentData);
            } else {
                // Create new student
                await this.createStudent(studentData);
            }
        } catch (error) {
            // Error is already handled in the API methods
        }
    }

    clearForm() {
        document.getElementById('studentForm').reset();
        document.getElementById('studentId').value = '';
    }

    // Delete Methods
    deleteStudent(id, name) {
        console.log('=== DELETE STUDENT DEBUG ===');
        console.log('deleteStudent called with id:', id, 'name:', name);
        console.log('id type:', typeof id);
        console.log('id value:', id);
        
        // Ensure id is a number
        const studentId = parseInt(id);
        console.log('parsed studentId:', studentId);
        
        document.getElementById('deleteStudentName').textContent = name;
        document.getElementById('deleteModal').setAttribute('data-student-id', studentId);
        
        console.log('Modal data-student-id set to:', studentId);
        console.log('Modal element:', document.getElementById('deleteModal'));
        
        this.deleteModal.show();
        console.log('Modal shown');
    }

    async confirmDelete() {
        console.log('=== CONFIRM DELETE DEBUG ===');
        const modal = document.getElementById('deleteModal');
        console.log('Modal element found:', modal);
        
        const studentId = modal.getAttribute('data-student-id');
        console.log('data-student-id attribute value:', studentId);
        console.log('studentId type:', typeof studentId);
        
        if (studentId && studentId !== 'null' && studentId !== 'undefined' && studentId !== '') {
            try {
                console.log('Proceeding with deletion of student ID:', studentId);
                
                const response = await fetch(`/api/students/${studentId}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                
                if (response.ok) {
                    console.log('Student deleted successfully');
                    this.showSuccess('Öğrenci başarıyla silindi!');
                    this.deleteModal.hide();
                    this.loadStudents(); 
                } else {
                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                }
                
                modal.removeAttribute('data-student-id');
                console.log('Modal data cleared');
            } catch (error) {
                console.error('Error in confirmDelete:', error);
                this.showError('Silme işlemi sırasında hata oluştu: ' + error.message);
            }
        } else {
            console.error('No valid student ID found in modal');
            console.error('studentId value:', studentId);
            console.error('studentId === null:', studentId === null);
            console.error('studentId === undefined:', studentId === undefined);
            console.error('studentId === "":', studentId === '');
            this.showError('Öğrenci ID bulunamadı! Lütfen tekrar deneyin.');
        }
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    formatDate(dateString) {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleDateString('tr-TR', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    showLoading(show) {
        const spinner = document.getElementById('loadingSpinner');
        if (show) {
            spinner.classList.remove('d-none');
        } else {
            spinner.classList.add('d-none');
        }
    }

    showNoResults() {
        document.getElementById('noResults').classList.remove('d-none');
    }

    hideNoResults() {
        document.getElementById('noResults').classList.add('d-none');
    }

    showSuccess(message) {
        document.getElementById('successMessage').textContent = message;
        this.successToast.show();
    }

    showError(message) {
        document.getElementById('errorMessage').textContent = message;
        this.errorToast.show();
    }
}

// Global functions for HTML onclick events
function showAddForm() {
    app.showAddForm();
}

function searchStudents() {
    const query = document.getElementById('searchInput').value.trim();
    if (query) {
        app.searchStudents(query);
    } else {
        app.loadStudents();
    }
}

function filterByAge() {
    app.filterByAge();
}

function saveStudent() {
    app.saveStudent();
}

function editStudent(id) {
    console.log('Global editStudent called with id:', id);
    if (window.app) {
        window.app.editStudent(id);
    } else {
        console.error('App instance not found');
    }
}

function viewStudent(id) {
    console.log('Global viewStudent called with id:', id);
    if (window.app) {
        window.app.viewStudent(id);
    } else {
        console.error('App instance not found');
    }
}

function deleteStudent(id, name) {
    console.log('Global deleteStudent called with id:', id, 'name:', name);
    if (window.app) {
        window.app.deleteStudent(id, name);
    } else {
        console.error('App instance not found');
    }
}

function confirmDelete() {
    console.log('Global confirmDelete called');
    if (window.app) {
        window.app.confirmDelete();
    } else {
        console.error('App instance not found');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    window.app = new StudentApp();
});
