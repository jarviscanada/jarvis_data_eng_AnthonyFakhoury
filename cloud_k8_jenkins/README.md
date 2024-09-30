# Cloud & DevOps Project
### Introduction
- **Application Architecture**: The application architecture includes components such as a **load balancer** to distribute traffic evenly across servers, **auto-scaling** to handle increased loads, and an **application server** for running backend services. The application interacts with **PostgreSQL (PSQL)** for database operations. The infrastructure is designed to ensure high availability, scalability, and efficient resource management.

- **Kubernetes Deployment**: The development environment uses a local Kubernetes cluster (e.g., **Minikube**), while the production environment uses a cloud-based Kubernetes service (e.g., **Azure Kubernetes Service (AKS)**). Both environments are defined using **Helm charts** for easy configuration and deployment. The production environment also includes **persistent volumes** for database storage and **auto-scaling** to manage traffic surges.

- **Jenkins CI/CD Pipeline**: The **Jenkins pipeline** is used to automate the CI/CD process. It starts with a **git clone** from the repository, followed by the **build** process using tools like Maven. Next, the application is **tested** using predefined unit and integration tests. If successful, the app is **deployed** to the Kubernetes environment using Helm and Kubernetes manifests.

### Application Architecture
- **Load Balancer**: Distributes requests to multiple application servers.
- **Auto-Scaling**: Automatically adjusts the number of running application servers based on traffic.
- **Application Server**: Handles the business logic of the application.
- **PostgreSQL**: Manages data storage and retrieval.
- **Kubernetes Deployment Diagram**: Shows how the different components (e.g., pods, services, config maps) interact in the deployment.

### Jenkins CI/CD Pipeline
- **Git Clone**: Fetches the latest code from the repository.
- **Build**: Compiles and packages the application.
- **Test**: Runs automated tests to validate the build.
- **Deploy**: Pushes the built application to Kubernetes using Helm.

### Improvements
1. **Enhance Auto-scaling**: Implement predictive auto-scaling based on historical data to handle traffic more efficiently.
2. **Optimize CI/CD Pipeline**: Add caching mechanisms to speed up the build process, especially when dependencies dont change.
3. **Increase Monitoring**: Add more in-depth application and infrastructure monitoring to preemptively identify and fix issues.
4. **Update API used**: Previous API was depreciated and is now out of service, need to correctly update images.